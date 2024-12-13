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
                '-o', 'ServerAliveCountMax=2',
                '-o', 'ServerAliveInterval=30',
                '-o', 'UserKnownHostsFile=/dev/null',
                '-o', 'StrictHostKeyChecking=no',
                '-o', 'LogLevel=ERROR'
                ]
        scp_options = [
                '-r'
        ]
        self.ssh = ['ssh', '-l', self.user ] + ssh_options
        self.scp = ['scp'] + ssh_options + scp_options
        if port:
            self.ssh = self.ssh + [ '-p', port ]
            self.scp = self.scp + [ '-P', port ]

    def start(self, **kwargs):
        pass

    def stop(self, **kwargs):
        pass

    def _run(self, command, timeout=None, ignore_status=True, raw=False):
        """
            Runs command in target using SSHProcess.
        """
        self.logger.debug("[Running]$ %s" % " ".join(command))

        starttime = time.time()
        status, output = SSHCall(command, self.logger, timeout, raw)
        self.logger.debug("[Command returned '%d' after %.2f seconds]"
                 "" % (status, time.time() - starttime))

        if status and not ignore_status:
            raise AssertionError("Command '%s' returned non-zero exit "
                                 "status %d:\n%s" % (command, status, output))

        return (status, output)

    def run(self, command, timeout=None, ignore_status=True, raw=False):
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

        status, output = self._run(sshCmd, processTimeout, ignore_status, raw)
        self.logger.debug('Command: %s\nStatus: %d Output:  %s\n' % (command, status, output))

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

def SSHCall(command, logger, timeout=None, raw=False, **opts):

    def run():
        nonlocal output
        nonlocal process
        output_raw = b''
        starttime = time.time()
        process = subprocess.Popen(command, **options)
        has_timeout = False
        if timeout:
            endtime = starttime + timeout
            eof = False
            os.set_blocking(process.stdout.fileno(), False)
            while not has_timeout and not eof:
                try:
                    logger.debug('Waiting for process output: time: %s, endtime: %s' % (time.time(), endtime))
                    if select.select([process.stdout], [], [], 5)[0] != []:
                        # wait a bit for more data, tries to avoid reading single characters
                        time.sleep(0.2)
                        data = process.stdout.read()
                        if not data:
                            eof = True
                        else:
                            output_raw += data
                            # ignore errors to capture as much as possible
                            logger.debug('Partial data from SSH call:\n%s' % data.decode('utf-8', errors='ignore'))
                            endtime = time.time() + timeout
                except InterruptedError:
                    logger.debug('InterruptedError')
                    continue
                except BlockingIOError:
                    logger.debug('BlockingIOError')
                    continue

                if time.time() >= endtime:
                    logger.debug('SSHCall has timeout! Time: %s, endtime: %s' % (time.time(), endtime))
                    has_timeout = True

            process.stdout.close()

            # process hasn't returned yet
            if not eof:
                process.terminate()
                time.sleep(5)
                try:
                    process.kill()
                except OSError:
                    logger.debug('OSError when killing process')
                    pass
                endtime = time.time() - starttime
                lastline = ("\nProcess killed - no output for %d seconds. Total"
                            " running time: %d seconds." % (timeout, endtime))
                logger.debug('Received data from SSH call:\n%s ' % lastline)
                output += lastline
                process.wait()

        else:
            output_raw = process.communicate()[0]

        output = output_raw if raw else output_raw.decode('utf-8', errors='ignore')
        logger.debug('Data from SSH call:\n%s' % output.rstrip())

        # timout or not, make sure process exits and is not hanging
        if process.returncode == None:
            try:
                process.wait(timeout=5)
            except TimeoutExpired:
                try:
                    process.kill()
                except OSError:
                    logger.debug('OSError')
                    pass
                process.wait()

        if has_timeout:
            # Version of openssh before 8.6_p1 returns error code 0 when killed
            # by a signal, when the timeout occurs we will receive a 0 error
            # code because the process is been terminated and it's wrong because
            # that value means success, but the process timed out.
            # Afterwards, from version 8.6_p1 onwards, the returned code is 255.
            # Fix this behaviour by checking the return code
            if process.returncode == 0:
                process.returncode = 255

    options = {
        "stdout": subprocess.PIPE,
        "stderr": subprocess.STDOUT if not raw else None,
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
        if process.returncode == None:
            process.wait()
        logger.debug('Something went wrong, killing SSH process')
        raise

    return (process.returncode, output if raw else output.rstrip())
