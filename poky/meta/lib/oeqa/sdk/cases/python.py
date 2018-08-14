import os
import shutil
import unittest

from oeqa.core.utils.path import remove_safe
from oeqa.sdk.case import OESDKTestCase

class PythonTest(OESDKTestCase):
    @classmethod
    def setUpClass(self):
        if not (self.tc.hasHostPackage("nativesdk-python") or
                self.tc.hasHostPackage("python-native")):
            raise unittest.SkipTest("No python package in the SDK")

        for f in ['test.py']:
            shutil.copyfile(os.path.join(self.tc.files_dir, f),
                   os.path.join(self.tc.sdk_dir, f))

    def test_python_exists(self):
        self._run('which python')

    def test_python_stdout(self):
        output = self._run('python %s/test.py' % self.tc.sdk_dir)
        self.assertEqual(output.strip(), "the value of a is 0.01", msg="Incorrect output: %s" % output)

    def test_python_testfile(self):
        self._run('ls /tmp/testfile.python')

    @classmethod
    def tearDownClass(self):
        remove_safe("%s/test.py" % self.tc.sdk_dir)
        remove_safe("/tmp/testfile.python")
