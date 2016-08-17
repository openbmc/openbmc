import unittest
import os
import shutil
from oeqa.oetest import oeSDKTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    if not oeSDKTest.hasHostPackage("nativesdk-perl"):
        skipModule("No perl package in the SDK")


class PerlTest(oeSDKTest):

    @classmethod
    def setUpClass(self):
        for f in ['test.pl']:
            shutil.copyfile(os.path.join(self.tc.filesdir, f), self.tc.sdktestdir + f)
        self.testfile = self.tc.sdktestdir + "test.pl"

    def test_perl_exists(self):
        self._run('which perl')

    def test_perl_works(self):
        self._run('perl %s/test.pl' % self.tc.sdktestdir)

    @classmethod
    def tearDownClass(self):
        bb.utils.remove("%s/test.pl" % self.tc.sdktestdir)
