#!/usr/bin/python

# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (C) 2015 Alexandru Damian for Intel Corp.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.


# Test definitions. The runner will look for and auto-discover the tests
# no matter what they file are they in, as long as they are in the same directory
# as this file.

import unittest
from shellutils import run_shell_cmd, ShellCmdException
import config

import pexpect
import sys, os, signal, time

class Test00PyCompilable(unittest.TestCase):
    ''' Verifies that all Python files are syntactically correct '''
    def test_compile_file(self):
        try:
            run_shell_cmd("find . -name *py -type f -print0 | xargs -0 -n1 -P20 python -m py_compile", config.TESTDIR)
        except ShellCmdException as exc:
            self.fail("Error compiling python files: %s" % (exc))

    def test_pylint_file(self):
        try:
            run_shell_cmd(r"find . -iname \"*\.py\" -type f -print0 | PYTHONPATH=${PYTHONPATH}:. xargs -r -0 -n1 pylint --load-plugins pylint_django -E --reports=n 2>&1", cwd=config.TESTDIR + "/bitbake/lib/toaster")
        except ShellCmdException as exc:
            self.fail("Pylint fails: %s\n" % exc)

class Test01PySystemStart(unittest.TestCase):
    ''' Attempts to start Toaster, verify that it is succesfull, and stop it '''
    def setUp(self):
        run_shell_cmd("bash -c 'rm -f build/*log'")

    def test_start_interactive_mode(self):
        try:
            run_shell_cmd("bash -c 'source %s/oe-init-build-env && source toaster start webport=%d && source toaster stop'" % (config.TESTDIR, config.TOASTER_PORT), config.TESTDIR)
        except ShellCmdException as exc:
            self.fail("Failed starting interactive mode: %s" % (exc))

    def test_start_managed_mode(self):
        try:
            run_shell_cmd("%s/bitbake/bin/toaster webport=%d nobrowser & sleep 10 && curl http://localhost:%d/ && kill -2 %%1" % (config.TESTDIR, config.TOASTER_PORT, config.TOASTER_PORT), config.TESTDIR)
        except ShellCmdException as exc:
            self.fail("Failed starting managed mode: %s" % (exc))

class Test02HTML5Compliance(unittest.TestCase):
    def setUp(self):
        self.origdir = os.getcwd()
        self.crtdir = os.path.dirname(config.TESTDIR)
        self.cleanup_database = False
        os.chdir(self.crtdir)
        if not os.path.exists(os.path.join(self.crtdir, "toaster.sqlite")):
            self.cleanup_database = True
            run_shell_cmd("%s/bitbake/lib/toaster/manage.py syncdb --noinput" % config.TESTDIR)
            run_shell_cmd("%s/bitbake/lib/toaster/manage.py migrate orm" % config.TESTDIR)
            run_shell_cmd("%s/bitbake/lib/toaster/manage.py migrate bldcontrol" % config.TESTDIR)
            run_shell_cmd("%s/bitbake/lib/toaster/manage.py loadconf %s/meta-yocto/conf/toasterconf.json" % (config.TESTDIR, config.TESTDIR))
            run_shell_cmd("%s/bitbake/lib/toaster/manage.py lsupdates" % config.TESTDIR)

            setup = pexpect.spawn("%s/bitbake/lib/toaster/manage.py checksettings" % config.TESTDIR)
            setup.logfile = sys.stdout
            setup.expect(r".*or type the full path to a different directory: ")
            setup.sendline('')
            setup.sendline('')
            setup.expect(r".*or type the full path to a different directory: ")
            setup.sendline('')
            setup.expect(r"Enter your option: ")
            setup.sendline('0')

        self.child = pexpect.spawn("bash", ["%s/bitbake/bin/toaster" % config.TESTDIR, "webport=%d" % config.TOASTER_PORT, "nobrowser"], cwd=self.crtdir)
        self.child.logfile = sys.stdout
        self.child.expect("Toaster is now running. You can stop it with Ctrl-C")

    def test_html5_compliance(self):
        import urllist, urlcheck
        results = {}
        for url in urllist.URLS:
            results[url] = urlcheck.validate_html5(config.TOASTER_BASEURL + url)

        failed = []
        for url in results:
            if results[url][1] != 0:
                failed.append((url, results[url]))


        self.assertTrue(len(failed) == 0, "Not all URLs validate: \n%s " % "\n".join(["".join(str(x)) for x in failed]))

        #(config.TOASTER_BASEURL + url, status, errors, warnings))

    def tearDown(self):
        while self.child.isalive():
            self.child.kill(signal.SIGINT)
            time.sleep(1)
        os.chdir(self.origdir)
        toaster_sqlite_path = os.path.join(self.crtdir, "toaster.sqlite")
        if self.cleanup_database and os.path.exists(toaster_sqlite_path):
            os.remove(toaster_sqlite_path)
