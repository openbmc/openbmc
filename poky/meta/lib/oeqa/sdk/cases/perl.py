import os
import shutil
import unittest

from oeqa.core.utils.path import remove_safe
from oeqa.sdk.case import OESDKTestCase

class PerlTest(OESDKTestCase):
    @classmethod
    def setUpClass(self):
        if not (self.tc.hasHostPackage("nativesdk-perl") or
                self.tc.hasHostPackage("perl-native")):
            raise unittest.SkipTest("No perl package in the SDK")

        for f in ['test.pl']:
            shutil.copyfile(os.path.join(self.tc.files_dir, f),
                    os.path.join(self.tc.sdk_dir, f))
        self.testfile = os.path.join(self.tc.sdk_dir, "test.pl")

    def test_perl_exists(self):
        self._run('which perl')

    def test_perl_works(self):
        self._run('perl %s' % self.testfile)

    @classmethod
    def tearDownClass(self):
        remove_safe(self.testfile)
