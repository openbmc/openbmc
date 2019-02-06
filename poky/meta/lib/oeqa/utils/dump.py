import os
import sys
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

    def _write_dump(self, command, output):
        if isinstance(self, HostDumper):
            prefix = "host"
        elif isinstance(self, TargetDumper):
            prefix = "target"
        else:
            prefix = "unknown"
        for i in itertools.count():
            filename = "%s_%02d_%s" % (prefix, i, command)
            fullname = os.path.join(self.dump_dir, filename)
            if not os.path.exists(fullname):
                break
        with open(fullname, 'w') as dump_file:
            dump_file.write(output)


class HostDumper(BaseDumper):
    """ Class to get dumps from the host running the tests """

    def __init__(self, cmds, parent_dir):
        super(HostDumper, self).__init__(cmds, parent_dir)

    def dump_host(self, dump_dir=""):
        if dump_dir:
            self.dump_dir = dump_dir
        for cmd in self.cmds:
            result = runCmd(cmd, ignore_status=True)
            self._write_dump(cmd.split()[0], result.output)

class TargetDumper(BaseDumper):
    """ Class to get dumps from target, it only works with QemuRunner """

    def __init__(self, cmds, parent_dir, runner):
        super(TargetDumper, self).__init__(cmds, parent_dir)
        self.runner = runner

    def dump_target(self, dump_dir=""):
        if dump_dir:
            self.dump_dir = dump_dir
        for cmd in self.cmds:
            # We can continue with the testing if serial commands fail
            try:
                (status, output) = self.runner.run_serial(cmd)
                self._write_dump(cmd.split()[0], output)
            except:
                print("Tried to dump info from target but "
                        "serial console failed")
