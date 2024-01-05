#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import collections
import os
import sys

from shutil import rmtree
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends

# importlib.resources.open_text in Python <3.10 doesn't search all directories
# when a package is split across multiple directories. Until we can rely on
# 3.10+, reimplement the searching logic.
if sys.version_info < (3, 10):
    def _open_text(package, resource):
        import importlib, pathlib
        module = importlib.import_module(package)
        for path in module.__path__:
            candidate = pathlib.Path(path) / resource
            if candidate.exists():
                return candidate.open(encoding='utf-8')
        raise FileNotFoundError
else:
    from importlib.resources import open_text as _open_text


class ParseLogsTest(OERuntimeTestCase):

    # Which log files should be collected
    log_locations = ["/var/log/", "/var/log/dmesg", "/tmp/dmesg_output.log"]

    # The keywords that identify error messages in the log files
    errors = ["error", "cannot", "can't", "failed"]

    # A list of error messages that should be ignored
    ignore_errors = []

    @classmethod
    def setUpClass(cls):
        # When systemd is enabled we need to notice errors on
        # circular dependencies in units.
        if 'systemd' in cls.td.get('DISTRO_FEATURES'):
            cls.errors.extend([
                'Found ordering cycle on',
                'Breaking ordering cycle by deleting job',
                'deleted to break ordering cycle',
                'Ordering cycle found, skipping',
                ])

        cls.errors = [s.casefold() for s in cls.errors]

        cls.load_machine_ignores()

    @classmethod
    def load_machine_ignores(cls):
        # Add TARGET_ARCH explicitly as not every machine has that in MACHINEOVERRDES (eg qemux86-64)
        for candidate in ["common", cls.td.get("TARGET_ARCH")] + cls.td.get("MACHINEOVERRIDES").split(":"):
            try:
                name = f"parselogs-ignores-{candidate}.txt"
                for line in _open_text("oeqa.runtime.cases", name):
                    line = line.strip()
                    if line and not line.startswith("#"):
                        cls.ignore_errors.append(line.casefold())
            except FileNotFoundError:
                pass

    # Go through the log locations provided and if it's a folder
    # create a list with all the .log files in it, if it's a file
    # just add it to that list.
    def getLogList(self, log_locations):
        logs = []
        for location in log_locations:
            status, _ = self.target.run('test -f %s' % location)
            if status == 0:
                logs.append(location)
            else:
                status, _ = self.target.run('test -d %s' % location)
                if status == 0:
                    cmd = 'find %s -name \\*.log -maxdepth 1 -type f' % location
                    status, output = self.target.run(cmd)
                    if status == 0:
                        output = output.splitlines()
                        for logfile in output:
                            logs.append(os.path.join(location, logfile))
        return logs

    # Copy the log files to be parsed locally
    def transfer_logs(self, log_list):
        workdir = self.td.get('WORKDIR')
        self.target_logs = workdir + '/' + 'target_logs'
        target_logs = self.target_logs
        if os.path.exists(target_logs):
            rmtree(self.target_logs)
        os.makedirs(target_logs)
        for f in log_list:
            self.target.copyFrom(str(f), target_logs)

    # Get the local list of logs
    def get_local_log_list(self, log_locations):
        self.transfer_logs(self.getLogList(log_locations))
        list_dir = os.listdir(self.target_logs)
        dir_files = [os.path.join(self.target_logs, f) for f in list_dir]
        logs = [f for f in dir_files if os.path.isfile(f)]
        return logs

    def get_context(self, lines, index, before=6, after=3):
        """
        Given a set of lines and the index of the line that is important, return
        a number of lines surrounding that line.
        """
        last = len(lines)

        start = index - before
        end = index + after + 1

        if start < 0:
            end -= start
            start = 0
        if end > last:
            start -= end - last
            end = last

        return lines[start:end]

    def test_get_context(self):
        """
        A test case for the test case.
        """
        lines = list(range(0,10))
        self.assertEqual(self.get_context(lines, 0, 2, 1), [0, 1, 2, 3])
        self.assertEqual(self.get_context(lines, 5, 2, 1), [3, 4, 5, 6])
        self.assertEqual(self.get_context(lines, 9, 2, 1), [6, 7, 8, 9])

    def parse_logs(self, logs, lines_before=10, lines_after=10):
        """
        Search the log files @logs looking for error lines (marked by
        @self.errors), ignoring anything listed in @self.ignore_errors.

        Returns a dictionary of log filenames to a dictionary of error lines to
        the error context (controlled by @lines_before and @lines_after).
        """
        results = collections.defaultdict(dict)

        for log in logs:
            with open(log) as f:
                lines = f.readlines()

            for i, line in enumerate(lines):
                line = line.strip()
                line_lower = line.casefold()

                if any(keyword in line_lower for keyword in self.errors):
                    if not any(ignore in line_lower for ignore in self.ignore_errors):
                        results[log][line] = "".join(self.get_context(lines, i, lines_before, lines_after))

        return results

    # Get the output of dmesg and write it in a file.
    # This file is added to log_locations.
    def write_dmesg(self):
        (status, dmesg) = self.target.run('dmesg > /tmp/dmesg_output.log')

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_parselogs(self):
        self.write_dmesg()
        log_list = self.get_local_log_list(self.log_locations)
        result = self.parse_logs(log_list)

        errcount = 0
        self.msg = ""
        for log in result:
            self.msg += 'Log: ' + log + '\n'
            self.msg += '-----------------------\n'
            for error in result[log]:
                errcount += 1
                self.msg += 'Central error: ' + error + '\n'
                self.msg +=  '***********************\n'
                self.msg +=  result[log][error] + '\n'
                self.msg +=  '***********************\n'
        self.msg += '%s errors found in logs.' % errcount
        self.assertEqual(errcount, 0, msg=self.msg)
