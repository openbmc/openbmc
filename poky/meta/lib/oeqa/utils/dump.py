#
# SPDX-License-Identifier: MIT
#

import os
import sys
import json
import errno
import datetime
import itertools
from .commands import runCmd

class BaseDumper(object):
    """ Base class to dump commands from host/target """

    def __init__(self, cmds, parent_dir):
        self.cmds = []
        # Some testing doesn't inherit testimage, so it is needed
        # to set some defaults.
        self.parent_dir = parent_dir
        self.dump_dir = parent_dir
        dft_cmds = """  top -bn1
                        iostat -x -z -N -d -p ALL 20 2
                        ps -ef
                        free
                        df
                        memstat
                        dmesg
                        ip -s link
                        netstat -an"""
        if not cmds:
            cmds = dft_cmds
        for cmd in cmds.split('\n'):
            cmd = cmd.lstrip()
            if not cmd or cmd[0] == '#':
                continue
            self.cmds.append(cmd)

    def create_dir(self, dir_suffix):
        dump_subdir = ("%s_%s" % (
                datetime.datetime.now().strftime('%Y%m%d%H%M'),
                dir_suffix))
        dump_dir = os.path.join(self.parent_dir, dump_subdir)
        try:
            os.makedirs(dump_dir)
        except OSError as err:
            if err.errno != errno.EEXIST:
                raise err
        self.dump_dir = dump_dir

    def _construct_filename(self, command):
        if isinstance(self, HostDumper):
            prefix = "host"
        elif isinstance(self, TargetDumper):
            prefix = "target"
        elif isinstance(self, MonitorDumper):
            prefix = "qmp"
        else:
            prefix = "unknown"
        for i in itertools.count():
            filename = "%s_%02d_%s" % (prefix, i, command)
            fullname = os.path.join(self.dump_dir, filename)
            if not os.path.exists(fullname):
                break
        return fullname

    def _write_dump(self, command, output):
        fullname = self._construct_filename(command)
        os.makedirs(os.path.dirname(fullname), exist_ok=True)
        if isinstance(self, MonitorDumper):
            with open(fullname, 'w') as json_file:
                json.dump(output, json_file, indent=4)
        else:
            with open(fullname, 'w') as dump_file:
                dump_file.write(output)

class HostDumper(BaseDumper):
    """ Class to get dumps from the host running the tests """

    def __init__(self, cmds, parent_dir):
        super(HostDumper, self).__init__(cmds, parent_dir)

    def dump_host(self, dump_dir=""):
        if dump_dir:
            self.dump_dir = dump_dir
        env = os.environ.copy()
        env['PATH'] = '/usr/sbin:/sbin:/usr/bin:/bin'
        env['COLUMNS'] = '9999'
        for cmd in self.cmds:
            result = runCmd(cmd, ignore_status=True, env=env)
            self._write_dump(cmd.split()[0], result.output)

class TargetDumper(BaseDumper):
    """ Class to get dumps from target, it only works with QemuRunner.
        Will give up permanently after 5 errors from running commands over
        serial console. This helps to end testing when target is really dead, hanging
        or unresponsive.
    """

    def __init__(self, cmds, parent_dir, runner):
        super(TargetDumper, self).__init__(cmds, parent_dir)
        self.runner = runner
        self.errors = 0

    def dump_target(self, dump_dir=""):
        if self.errors >= 5:
                print("Too many errors when dumping data from target, assuming it is dead! Will not dump data anymore!")
                return
        if dump_dir:
            self.dump_dir = dump_dir
        for cmd in self.cmds:
            # We can continue with the testing if serial commands fail
            try:
                (status, output) = self.runner.run_serial(cmd)
                if status == 0:
                    self.errors = self.errors + 1
                self._write_dump(cmd.split()[0], output)
            except:
                self.errors = self.errors + 1
                print("Tried to dump info from target but "
                        "serial console failed")
                print("Failed CMD: %s" % (cmd))

class MonitorDumper(BaseDumper):
    """ Class to get dumps via the Qemu Monitor, it only works with QemuRunner
        Will stop completely if there are more than 5 errors when dumping monitor data.
        This helps to end testing when target is really dead, hanging or unresponsive.
    """

    def __init__(self, cmds, parent_dir, runner):
        super(MonitorDumper, self).__init__(cmds, parent_dir)
        self.runner = runner
        self.errors = 0

    def dump_monitor(self, dump_dir=""):
        if self.runner is None:
            return
        if dump_dir:
            self.dump_dir = dump_dir
        if self.errors >= 5:
                print("Too many errors when dumping data from qemu monitor, assuming it is dead! Will not dump data anymore!")
                return
        for cmd in self.cmds:
            cmd_name = cmd.split()[0]
            try:
                if len(cmd.split()) > 1:
                    cmd_args = cmd.split()[1]
                    if "%s" in cmd_args:
                        filename = self._construct_filename(cmd_name)
                    cmd_data = json.loads(cmd_args % (filename))
                    output = self.runner.run_monitor(cmd_name, cmd_data)
                else:
                    output = self.runner.run_monitor(cmd_name)
                self._write_dump(cmd_name, output)
            except Exception as e:
                self.errors = self.errors + 1
                print("Failed to dump QMP CMD: %s with\nException: %s" % (cmd_name, e))
