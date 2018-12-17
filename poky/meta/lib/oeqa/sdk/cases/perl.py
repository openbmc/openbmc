import unittest
from oeqa.sdk.case import OESDKTestCase

class PerlTest(OESDKTestCase):
    @classmethod
    def setUpClass(self):
        if not (self.tc.hasHostPackage("nativesdk-perl") or
                self.tc.hasHostPackage("perl-native")):
            raise unittest.SkipTest("No perl package in the SDK")

    def test_perl(self):
        try:
            cmd = "perl -e '$_=\"Uryyb, jbeyq\"; tr/a-zA-Z/n-za-mN-ZA-M/;print'"
            output = self._run(cmd)
            self.assertEqual(output, "Hello, world")
        except subprocess.CalledProcessError as e:
            self.fail("Unexpected exit %d (output %s)" % (e.returncode, e.output))
