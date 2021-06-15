#
# Copyright (C) 2013 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

# Provides a class for setting up ssh connections,
# running commands and copying files to/from a target.
# It's used by testimage.bbclass and tests in lib/oeqa/runtime.

import subprocess
import time
import os
import select


class SSHProcess(object):
    def __init__(self, **options):

        self.defaultopts = {
            "stdout": subprocess.PIPE,
            "stderr": subprocess.STDOUT,
            "stdin": None,
            "shell": False,
            "bufsize": -1,
            "start_new_session": True,
        }
        self.options = dict(self.defaultopts)
        self.options.update(options)
        self.status = None
        self.output = None
        self.process = None
        self.starttime = None
        self.logfile = None

        # Unset DISPLAY which means we won't trigger SSH_ASKPASS
        env = os.environ.copy()
        if "DISPLAY" in env:
            del env['DISPLAY']
        self.options['env'] = env

    def log(self, msg):
        if self.logfile:
            with open(self.logfile, "a") as f:
               f.write("%s" % msg)

    def _run(self, command, timeout=None, logfile=None):
        self.logfile = logfile
        self.starttime = time.time()
        output = ''
        self.process = subprocess.Popen(command, **self.options)
        if timeout:
            endtime = self.starttime + timeout
            eof = False
            while time.time() < endtime and not eof:
                try:
                    if select.select([self.process.stdout], [], [], 5)[0] != []:
                        data = os.read(self.process.stdout.fileno(), 1024)
                        if not data:
                            self.process.stdout.close()
                            eof = True
                        else:
                            data = data.decode("utf-8")
                            output += data
                            self.log(data)
                            endtime = time.time() + timeout
                except InterruptedError:
                    continue

            # process hasn't returned yet
            if not eof:
                self.process.terminate()
                time.sleep(5)
                try:
                    self.process.kill()
                except OSError:
                    pass
                lastline = "\nProcess killed - no output for %d seconds. Total running time: %d seconds." % (timeout, time.time() - self.starttime)
                self.log(lastline)
                output += lastline
        else:
            output = self.process.communicate()[0]
            self.log(output.rstrip())

        self.status = self.process.wait()
        self.output = output.rstrip()

    def run(self, command, timeout=None, logfile=None):
        try:
            self._run(command, timeout, logfile)
        except:
            # Need to guard against a SystemExit or other exception occuring whilst running
            # and ensure we don't leave a process behind.
            if self.process.poll() is None:
                self.process.kill()
                self.status = self.process.wait()
            raise
        return (self.status, self.output)

class SSHControl(object):
    def __init__(self, ip, logfile=None, timeout=300, user='root', port=None):
        self.ip = ip
        self.defaulttimeout = timeout
        self.ignore_status = True
        self.logfile = logfile
        self.user = user
        self.ssh_options = [
                '-o', 'UserKnownHostsFile=/dev/null',
                '-o', 'StrictHostKeyChecking=no',
                '-o', 'LogLevel=ERROR'
                ]
        self.ssh = ['ssh', '-l', self.user ] + self.ssh_options
        self.scp = ['scp'] + self.ssh_options
        if port:
            self.ssh = self.ssh + [ '-p', port ]
            self.scp = self.scp + [ '-P', port ]

    def log(self, msg):
        if self.logfile:
            with open(self.logfile, "a") as f:
                f.write("%s\n" % msg)

    def _internal_run(self, command, timeout=None, ignore_status = True):
        self.log("[Running]$ %s" % " ".join(command))

        proc = SSHProcess()
        status, output = proc.run(command, timeout, logfile=self.logfile)

        self.log("[Command returned '%d' after %.2f seconds]" % (status, time.time() - proc.starttime))

        if status and not ignore_status:
            raise AssertionError("Command '%s' returned non-zero exit status %d:\n%s" % (command, status, output))

        return (status, output)

    def run(self, command, timeout=None):
        """
        command - ssh command to run
        timeout=<val> - kill command if there is no output after <val> seconds
        timeout=None - kill command if there is no output after a default value seconds
        timeout=0 - no timeout, let command run until it returns
        """

        command = self.ssh + [self.ip, 'export PATH=/usr/sbin:/sbin:/usr/bin:/bin; ' + command]

        if timeout is None:
            return self._internal_run(command, self.defaulttimeout, self.ignore_status)
        if timeout == 0:
            return self._internal_run(command, None, self.ignore_status)
        return self._internal_run(command, timeout, self.ignore_status)

    def copy_to(self, localpath, remotepath):
        if os.path.islink(localpath):
            localpath = os.path.dirname(localpath) + "/" + os.readlink(localpath)
        command = self.scp + [localpath, '%s@%s:%s' % (self.user, self.ip, remotepath)]
        return self._internal_run(command, ignore_status=False)

    def copy_from(self, remotepath, localpath):
        command = self.scp + ['%s@%s:%s' % (self.user, self.ip, remotepath), localpath]
        return self._internal_run(command, ignore_status=False)

    def copy_dir_to(self, localpath, remotepath):
        """
        Copy recursively localpath directory to remotepath in target.
        """

        for root, dirs, files in os.walk(localpath):
            # Create directories in the target as needed
            for d in dirs:
                tmp_dir = os.path.join(root, d).replace(localpath, "")
                new_dir = os.path.join(remotepath, tmp_dir.lstrip("/"))
                cmd = "mkdir -p %s" % new_dir
                self.run(cmd)

            # Copy files into the target
            for f in files:
                tmp_file = os.path.join(root, f).replace(localpath, "")
                dst_file = os.path.join(remotepath, tmp_file.lstrip("/"))
                src_file = os.path.join(root, f)
                self.copy_to(src_file, dst_file)


    def delete_files(self, remotepath, files):
        """
        Delete files in target's remote path.
        """

        cmd = "rm"
        if not isinstance(files, list):
            files = [files]

        for f in files:
            cmd = "%s %s" % (cmd, os.path.join(remotepath, f))

        self.run(cmd)


    def delete_dir(self, remotepath):
        """
        Delete remotepath directory in target.
        """

        cmd = "rmdir %s" % remotepath
        self.run(cmd)


    def delete_dir_structure(self, localpath, remotepath):
        """
        Delete recursively localpath structure directory in target's remotepath.

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

        for root, dirs, files in os.walk(localpath, topdown=False):
            # Delete files first
            tmpdir = os.path.join(root).replace(localpath, "")
            remotedir = os.path.join(remotepath, tmpdir.lstrip("/"))
            self.delete_files(remotedir, files)

            # Remove dirs if empty
            for d in dirs:
                tmpdir = os.path.join(root, d).replace(localpath, "")
                remotedir = os.path.join(remotepath, tmpdir.lstrip("/"))
                self.delete_dir(remotepath)
