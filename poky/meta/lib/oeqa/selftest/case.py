#
# Copyright (C) 2013-2017 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import sys
import os
import glob
import errno
from unittest.util import safe_repr

import oeqa.utils.ftools as ftools
from oeqa.utils.commands import runCmd, bitbake, get_bb_var
from oeqa.core.case import OETestCase

import bb.utils

class OESelftestTestCase(OETestCase):
    def __init__(self, methodName="runTest"):
        self._extra_tear_down_commands = []
        super(OESelftestTestCase, self).__init__(methodName)

    @classmethod
    def setUpClass(cls):
        super(OESelftestTestCase, cls).setUpClass()

        cls.testlayer_path = cls.tc.config_paths['testlayer_path']
        cls.builddir = cls.tc.config_paths['builddir']

        cls.localconf_path = cls.tc.config_paths['localconf']
        cls.local_bblayers_path = cls.tc.config_paths['bblayers']

        cls.testinc_path = os.path.join(cls.tc.config_paths['builddir'],
                "conf/selftest.inc")
        cls.testinc_bblayers_path = os.path.join(cls.tc.config_paths['builddir'],
                "conf/bblayers.inc")
        cls.machineinc_path = os.path.join(cls.tc.config_paths['builddir'],
                "conf/machine.inc")

        cls._track_for_cleanup = [
            cls.testinc_path, cls.testinc_bblayers_path,
            cls.machineinc_path]

        cls.add_include()

    @classmethod
    def tearDownClass(cls):
        cls.remove_include()
        cls.remove_inc_files()
        super(OESelftestTestCase, cls).tearDownClass()

    @classmethod
    def add_include(cls):
        if "#include added by oe-selftest" \
            not in ftools.read_file(os.path.join(cls.builddir, "conf/local.conf")):
                cls.logger.info("Adding: \"include selftest.inc\" in %s" % os.path.join(cls.builddir, "conf/local.conf"))
                ftools.append_file(os.path.join(cls.builddir, "conf/local.conf"), \
                        "\n#include added by oe-selftest\ninclude machine.inc\ninclude selftest.inc")

        if "#include added by oe-selftest" \
            not in ftools.read_file(os.path.join(cls.builddir, "conf/bblayers.conf")):
                cls.logger.info("Adding: \"include bblayers.inc\" in bblayers.conf")
                ftools.append_file(os.path.join(cls.builddir, "conf/bblayers.conf"), \
                        "\n#include added by oe-selftest\ninclude bblayers.inc")

    @classmethod
    def remove_include(cls):
        if "#include added by oe-selftest.py" \
            in ftools.read_file(os.path.join(cls.builddir, "conf/local.conf")):
                cls.logger.info("Removing the include from local.conf")
                ftools.remove_from_file(os.path.join(cls.builddir, "conf/local.conf"), \
                        "\n#include added by oe-selftest.py\ninclude machine.inc\ninclude selftest.inc")

        if "#include added by oe-selftest.py" \
            in ftools.read_file(os.path.join(cls.builddir, "conf/bblayers.conf")):
                cls.logger.info("Removing the include from bblayers.conf")
                ftools.remove_from_file(os.path.join(cls.builddir, "conf/bblayers.conf"), \
                        "\n#include added by oe-selftest.py\ninclude bblayers.inc")

    @classmethod
    def remove_inc_files(cls):
        try:
            os.remove(os.path.join(cls.builddir, "conf/selftest.inc"))
            for root, _, files in os.walk(cls.testlayer_path):
                for f in files:
                    if f == 'test_recipe.inc':
                        os.remove(os.path.join(root, f))
        except OSError as e:
            pass

        for incl_file in ['conf/bblayers.inc', 'conf/machine.inc']:
            try:
                os.remove(os.path.join(cls.builddir, incl_file))
            except:
                pass

    def setUp(self):
        super(OESelftestTestCase, self).setUp()
        os.chdir(self.builddir)
        # we don't know what the previous test left around in config or inc files
        # if it failed so we need a fresh start
        try:
            os.remove(self.testinc_path)
        except OSError as e:
            if e.errno != errno.ENOENT:
                raise
        for root, _, files in os.walk(self.testlayer_path):
            for f in files:
                if f == 'test_recipe.inc':
                    os.remove(os.path.join(root, f))

        for incl_file in [self.testinc_bblayers_path, self.machineinc_path]:
            try:
                os.remove(incl_file)
            except OSError as e:
                if e.errno != errno.ENOENT:
                    raise

        # tests might need their own setup
        # but if they overwrite this one they have to call
        # super each time, so let's give them an alternative
        self.setUpLocal()

    def setUpLocal(self):
        pass

    def tearDown(self):
        if self._extra_tear_down_commands:
            failed_extra_commands = []
            for command in self._extra_tear_down_commands:
                result = runCmd(command, ignore_status=True)
                if not result.status ==  0:
                    failed_extra_commands.append(command)
            if failed_extra_commands:
                self.logger.warning("tearDown commands have failed: %s" % ', '.join(map(str, failed_extra_commands)))
                self.logger.debug("Trying to move on.")
            self._extra_tear_down_commands = []

        if self._track_for_cleanup:
            for path in self._track_for_cleanup:
                if os.path.isdir(path):
                    bb.utils.remove(path, recurse=True)
                if os.path.isfile(path):
                    os.remove(path)
            self._track_for_cleanup = []

        self.tearDownLocal()
        super(OESelftestTestCase, self).tearDown()

    def tearDownLocal(self):
        pass

    def add_command_to_tearDown(self, command):
        """Add test specific commands to the tearDown method"""
        self.logger.debug("Adding command '%s' to tearDown for this test." % command)
        self._extra_tear_down_commands.append(command)

    def track_for_cleanup(self, path):
        """Add test specific files or directories to be removed in the tearDown method"""
        self.logger.debug("Adding path '%s' to be cleaned up when test is over" % path)
        self._track_for_cleanup.append(path)

    def write_config(self, data, multiconfig=None):
        """Write to config file"""
        if multiconfig:
            multiconfigdir = "%s/conf/multiconfig" % self.builddir
            os.makedirs(multiconfigdir, exist_ok=True)
            dest_path = '%s/%s.conf' % (multiconfigdir, multiconfig)
            self.track_for_cleanup(dest_path)
        else:
            dest_path = self.testinc_path

        self.logger.debug("Writing to: %s\n%s\n" % (dest_path, data))
        ftools.write_file(dest_path, data)

    def append_config(self, data):
        """Append to <builddir>/conf/selftest.inc"""
        self.logger.debug("Appending to: %s\n%s\n" % (self.testinc_path, data))
        ftools.append_file(self.testinc_path, data)

    def remove_config(self, data):
        """Remove data from <builddir>/conf/selftest.inc"""
        self.logger.debug("Removing from: %s\n%s\n" % (self.testinc_path, data))
        ftools.remove_from_file(self.testinc_path, data)

    def recipeinc(self, recipe):
        """Return absolute path of meta-selftest/recipes-test/<recipe>/test_recipe.inc"""
        return os.path.join(self.testlayer_path, 'recipes-test', recipe, 'test_recipe.inc')

    def write_recipeinc(self, recipe, data):
        """Write to meta-selftest/recipes-test/<recipe>/test_recipe.inc"""
        inc_file = self.recipeinc(recipe)
        self.logger.debug("Writing to: %s\n%s\n" % (inc_file, data))
        ftools.write_file(inc_file, data)
        return inc_file

    def append_recipeinc(self, recipe, data):
        """Append data to meta-selftest/recipes-test/<recipe>/test_recipe.inc"""
        inc_file = self.recipeinc(recipe)
        self.logger.debug("Appending to: %s\n%s\n" % (inc_file, data))
        ftools.append_file(inc_file, data)
        return inc_file

    def remove_recipeinc(self, recipe, data):
        """Remove data from meta-selftest/recipes-test/<recipe>/test_recipe.inc"""
        inc_file = self.recipeinc(recipe)
        self.logger.debug("Removing from: %s\n%s\n" % (inc_file, data))
        ftools.remove_from_file(inc_file, data)

    def delete_recipeinc(self, recipe):
        """Delete meta-selftest/recipes-test/<recipe>/test_recipe.inc file"""
        inc_file = self.recipeinc(recipe)
        self.logger.debug("Deleting file: %s" % inc_file)
        try:
            os.remove(inc_file)
        except OSError as e:
            if e.errno != errno.ENOENT:
                raise
    def write_bblayers_config(self, data):
        """Write to <builddir>/conf/bblayers.inc"""
        self.logger.debug("Writing to: %s\n%s\n" % (self.testinc_bblayers_path, data))
        ftools.write_file(self.testinc_bblayers_path, data)

    def append_bblayers_config(self, data):
        """Append to <builddir>/conf/bblayers.inc"""
        self.logger.debug("Appending to: %s\n%s\n" % (self.testinc_bblayers_path, data))
        ftools.append_file(self.testinc_bblayers_path, data)

    def remove_bblayers_config(self, data):
        """Remove data from <builddir>/conf/bblayers.inc"""
        self.logger.debug("Removing from: %s\n%s\n" % (self.testinc_bblayers_path, data))
        ftools.remove_from_file(self.testinc_bblayers_path, data)

    def set_machine_config(self, data):
        """Write to <builddir>/conf/machine.inc"""
        self.logger.debug("Writing to: %s\n%s\n" % (self.machineinc_path, data))
        ftools.write_file(self.machineinc_path, data)

    def disable_class(self, classname):
        destfile = "%s/classes/%s.bbclass" % (self.builddir, classname)
        os.makedirs(os.path.dirname(destfile), exist_ok=True)
        self.track_for_cleanup(destfile)
        self.logger.debug("Creating empty class: %s\n" % (destfile))
        ftools.write_file(destfile, "")

    # check does path exist
    def assertExists(self, expr, msg=None):
        if not os.path.exists(expr):
            msg = self._formatMessage(msg, "%s does not exist" % safe_repr(expr))
            raise self.failureException(msg)

    # check does path not exist
    def assertNotExists(self, expr, msg=None):
        if os.path.exists(expr):
            msg = self._formatMessage(msg, "%s exists when it should not" % safe_repr(expr))
            raise self.failureException(msg)
