#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import os
import time
import select
import logging
import subprocess
import codecs

from . import OETarget

class OESSHTarget(OETarget):
    def __init__(self, logger, ip, server_ip, timeout=300, user='root',
                 port=None, server_port=0, **kwargs):
        if not logger:
            logger = logging.getLogger('target')
            logger.setLevel(logging.INFO)
            filePath = os.path.join(os.getcwd(), 'remoteTarget.log')
            fileHandler = logging.FileHandler(filePath, 'w', 'utf-8')
            formatter = logging.Formatter(
                        '%(asctime)s.%(msecs)03d %(levelname)s: %(message)s',
                        '%H:%M:%S')
            fileHandler.setFormatter(formatter)
            logger.addHandler(fileHandler)

        super(OESSHTarget, self).__init__(logger)
        self.ip = ip
        self.server_ip = server_ip
        self.server_port = server_port
        self.timeout = timeout
        self.user = user
        ssh_options = [
                '-o', 'UserKnownHostsFile=/dev/null',
                '-o', 'StrictHostKeyChecking=no',
                '-o', 'LogLevel=ERROR'
                ]
        self.ssh = ['ssh', '-l', self.user ] + ssh_options
        self.scp = ['scp'] + ssh_options
        if port:
            self.ssh = self.ssh + [ '-p', port ]
            self.scp = self.scp + [ '-P', port ]

    def start(self, **kwargs):
        pass

    def stop(self, **kwargs):
        pass

    def _run(self, command, timeout=None, ignore_status=True):
        """
            Runs command in target using SSHProcess.
        """
        self.logger.debug("[Running]$ %s" % " ".join(command))

        starttime = time.time()
        status, output = SSHCall(command, self.logger, timeout)
        self.logger.debug("[Command returned '%d' after %.2f seconds]"
                 "" % (status, time.time() - starttime))

        if status and not ignore_status:
            raise AssertionError("Command '%s' returned non-zero exit "
                                 "status %d:\n%s" % (command, status, output))

        return (status, output)

    def run(self, command, timeout=None):
        """
            Runs command in target.

            command:    Command to run on target.
            timeout:    <value>:    Kill command after <val> seconds.
                        None:       Kill command default value seconds.
                        0:          No timeout, runs until return.
        """
        targetCmd = 'export PATH=/usr/sbin:/sbin:/usr/bin:/bin; %s' % command
        sshCmd = self.ssh + [self.ip, targetCmd]

        if timeout:
            processTimeout = timeout
        elif timeout==0:
            processTimeout = None
        else:
            processTimeout = self.timeout

        status, output = self._run(sshCmd, processTimeout, True)
        self.logger.debug('Command: %s\nOutput:  %s\n' % (command, output))
        if (status == 255) and (('No route to host') in output):
            self.target_dumper.dump_target()
        return (status, output)

    def copyTo(self, localSrc, remoteDst):
        """
            Copy file to target.

            If local file is symlink, recreate symlink in target.
        """
        if os.path.islink(localSrc):
            link = os.readlink(localSrc)
            dstDir, dstBase = os.path.split(remoteDst)
            sshCmd = 'cd %s; ln -s %s %s' % (dstDir, link, dstBase)
            return self.run(sshCmd)

        else:
            remotePath = '%s@%s:%s' % (self.user, self.ip, remoteDst)
            scpCmd = self.scp + [localSrc, remotePath]
            return self._run(scpCmd, ignore_status=False)

    def copyFrom(self, remoteSrc, localDst, warn_on_failure=False):
        """
            Copy file from target.
        """
        remotePath = '%s@%s:%s' % (self.user, self.ip, remoteSrc)
        scpCmd = self.scp + [remotePath, localDst]
        (status, output) = self._run(scpCmd, ignore_status=warn_on_failure)
        if warn_on_failure and status:
            self.logger.warning("Copy returned non-zero exit status %d:\n%s" % (status, output))
        return (status, output)

    def copyDirTo(self, localSrc, remoteDst):
        """
            Copy recursively localSrc directory to remoteDst in target.
        """

        for root, dirs, files in os.walk(localSrc):
            # Create directories in the target as needed
            for d in dirs:
                tmpDir = os.path.join(root, d).replace(localSrc, "")
                newDir = os.path.join(remoteDst, tmpDir.lstrip("/"))
                cmd = "mkdir -p %s" % newDir
                self.run(cmd)

            # Copy files into the target
            for f in files:
                tmpFile = os.path.join(root, f).replace(localSrc, "")
                dstFile = os.path.join(remoteDst, tmpFile.lstrip("/"))
                srcFile = os.path.join(root, f)
                self.copyTo(srcFile, dstFile)

    def deleteFiles(self, remotePath, files):
        """
            Deletes files in target's remotePath.
        """

        cmd = "rm"
        if not isinstance(files, list):
            files = [files]

        for f in files:
            cmd = "%s %s" % (cmd, os.path.join(remotePath, f))

        self.run(cmd)


    def deleteDir(self, remotePath):
        """
            Deletes target's remotePath directory.
        """

        cmd = "rmdir %s" % remotePath
        self.run(cmd)


    def deleteDirStructure(self, localPath, remotePath):
        """
        Delete recursively localPath structure directory in target's remotePath.

        This function is very usefult to delete a package that is installed in
        the DUT and the host running the test has such package extracted in tmp
        directory.

        Example:
            pwd: /home/user/tmp
            tree:   .
                    └── work
                        ├── dir1
                        │   └── file1
                        └── dir2

            localpath = "/home/user/tmp" and remotepath = "/home/user"

            With the above variables this function will try to delete the
            directory in the DUT in this order:
                /home/user/work/dir1/file1
                /home/user/work/dir1        (if dir is empty)
                /home/user/work/dir2        (if dir is empty)
                /home/user/work             (if dir is empty)
        """

        for root, dirs, files in os.walk(localPath, topdown=False):
            # Delete files first
            tmpDir = os.path.join(root).replace(localPath, "")
            remoteDir = os.path.join(remotePath, tmpDir.lstrip("/"))
            self.deleteFiles(remoteDir, files)

            # Remove dirs if empty
            for d in dirs:
                tmpDir = os.path.join(root, d).replace(localPath, "")
                remoteDir = os.path.join(remotePath, tmpDir.lstrip("/"))
                self.deleteDir(remoteDir)

def SSHCall(command, logger, timeout=None, **opts):

    def run():
        nonlocal output
        nonlocal process
        starttime = time.time()
        process = subprocess.Popen(command, **options)
        if timeout:
            endtime = starttime + timeout
            eof = False
            while time.time() < endtime and not eof:
                logger.debug('time: %s, endtime: %s' % (time.time(), endtime))
                try:
                    if select.select([process.stdout], [], [], 5)[0] != []:
                        reader = codecs.getreader('utf-8')(process.stdout, 'ignore')
                        data = reader.read(1024, 4096)
                        if not data:
                            process.stdout.close()
                            eof = True
                        else:
                            output += data
                            logger.debug('Partial data from SSH call: %s' % data)
                            endtime = time.time() + timeout
                except InterruptedError:
                    continue

            # process hasn't returned yet
            if not eof:
                process.terminate()
                time.sleep(5)
                try:
                    process.kill()
                except OSError:
                    pass
                endtime = time.time() - starttime
                lastline = ("\nProcess killed - no output for %d seconds. Total"
                            " running time: %d seconds." % (timeout, endtime))
                logger.debug('Received data from SSH call %s ' % lastline)
                output += lastline

        else:
            output = process.communicate()[0].decode('utf-8', errors='ignore')
            logger.debug('Data from SSH call: %s' % output.rstrip())

    options = {
        "stdout": subprocess.PIPE,
        "stderr": subprocess.STDOUT,
        "stdin": None,
        "shell": False,
        "bufsize": -1,
        "start_new_session": True,
    }
    options.update(opts)
    output = ''
    process = None

    # Unset DISPLAY which means we won't trigger SSH_ASKPASS
    env = os.environ.copy()
    if "DISPLAY" in env:
        del env['DISPLAY']
    options['env'] = env

    try:
        run()
    except:
        # Need to guard against a SystemExit or other exception ocurring
        # whilst running and ensure we don't leave a process behind.
        if process.poll() is None:
            process.kill()
        logger.debug('Something went wrong, killing SSH process')
        raise
    return (process.wait(), output.rstrip())
