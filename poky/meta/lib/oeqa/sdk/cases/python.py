import subprocess, unittest
from oeqa.sdk.case import OESDKTestCase

class PythonTest(OESDKTestCase):
    @classmethod
    def setUpClass(self):
        if not (self.tc.hasHostPackage("nativesdk-python3") or
                self.tc.hasHostPackage("python3-native")):
            raise unittest.SkipTest("No python package in the SDK")

    def test_python3(self):
        try:
            cmd = "python3 -c \"import codecs; print(codecs.encode('Uryyb, jbeyq', 'rot13'))\""
            output = self._run(cmd)
            self.assertEqual(output, "Hello, world\n")
        except subprocess.CalledProcessError as e:
            self.fail("Unexpected exit %d (output %s)" % (e.returncode, e.output))
