import unittest
import os
import shutil
from oeqa.oetest import oeSDKTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    machine = oeSDKTest.tc.d.getVar("MACHINE", True)
    if not oeSDKTest.hasHostPackage("packagegroup-cross-canadian-" + machine):
        skipModule("SDK doesn't contain a cross-canadian toolchain")


class GccCompileTest(oeSDKTest):

    @classmethod
    def setUpClass(self):
        for f in ['test.c', 'test.cpp', 'testsdkmakefile']:
            shutil.copyfile(os.path.join(self.tc.filesdir, f), self.tc.sdktestdir + f)

    def test_gcc_compile(self):
        self._run('$CC %s/test.c -o %s/test -lm' % (self.tc.sdktestdir, self.tc.sdktestdir))

    def test_gpp_compile(self):
        self._run('$CXX %s/test.c -o %s/test -lm' % (self.tc.sdktestdir, self.tc.sdktestdir))

    def test_gpp2_compile(self):
        self._run('$CXX %s/test.cpp -o %s/test -lm' % (self.tc.sdktestdir, self.tc.sdktestdir))

    def test_make(self):
        self._run('cd %s; make -f testsdkmakefile' % self.tc.sdktestdir)

    @classmethod
    def tearDownClass(self):
        files = [self.tc.sdktestdir + f for f in ['test.c', 'test.cpp', 'test.o', 'test', 'testsdkmakefile']]
        for f in files:
            bb.utils.remove(f)
