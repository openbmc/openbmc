#
# SPDX-License-Identifier: MIT
#

import os
import shutil
import unittest

from oeqa.core.utils.path import remove_safe
from oeqa.sdk.case import OESDKTestCase

from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class GccCompileTest(OESDKTestCase):
    td_vars = ['MACHINE']

    @classmethod
    def setUpClass(self):
        files = {'test.c' : self.tc.files_dir, 'test.cpp' : self.tc.files_dir,
                'testsdkmakefile' : self.tc.sdk_files_dir} 
        for f in files:
            shutil.copyfile(os.path.join(files[f], f),
                    os.path.join(self.tc.sdk_dir, f))

    def setUp(self):
        machine = self.td.get("MACHINE")
        if not (self.tc.hasHostPackage("packagegroup-cross-canadian-%s" % machine) or
                self.tc.hasHostPackage("^gcc-", regex=True)):
            raise unittest.SkipTest("GccCompileTest class: SDK doesn't contain a cross-canadian toolchain")

    def test_gcc_compile(self):
        self._run('$CC %s/test.c -o %s/test -lm' % (self.tc.sdk_dir, self.tc.sdk_dir))

    def test_gpp_compile(self):
        self._run('$CXX %s/test.c -o %s/test -lm' % (self.tc.sdk_dir, self.tc.sdk_dir))

    def test_gpp2_compile(self):
        self._run('$CXX %s/test.cpp -o %s/test -lm' % (self.tc.sdk_dir, self.tc.sdk_dir))

    def test_make(self):
        self._run('cd %s; make -f testsdkmakefile' % self.tc.sdk_dir)

    @classmethod
    def tearDownClass(self):
        files = [os.path.join(self.tc.sdk_dir, f) \
                for f in ['test.c', 'test.cpp', 'test.o', 'test',
                    'testsdkmakefile']]
        for f in files:
            remove_safe(f)
