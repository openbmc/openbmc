# Copyright (C) 2013 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

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
            "preexec_fn": os.setsid,
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
                if select.select([self.process.stdout], [], [], 5)[0] != []:
                    data = os.read(self.process.stdout.fileno(), 1024)
                    if not data:
                        self.process.stdout.close()
                        eof = True
                    else:
                        output += data
                        self.log(data)
                        endtime = time.time() + timeout


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
        command = self.scp + [localpath, '%s@%s:%s' % (self.user, self.ip, remotepath)]
        return self._internal_run(command, ignore_status=False)

    def copy_from(self, remotepath, localpath):
        command = self.scp + ['%s@%s:%s' % (self.user, self.ip, remotepath), localpath]
        return self._internal_run(command, ignore_status=False)
