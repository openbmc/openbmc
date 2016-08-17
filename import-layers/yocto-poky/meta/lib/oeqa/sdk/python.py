import unittest
import os
import shutil
from oeqa.oetest import oeSDKTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    if not oeSDKTest.hasHostPackage("nativesdk-python"):
        skipModule("No python package in the SDK")


class PythonTest(oeSDKTest):

    @classmethod
    def setUpClass(self):
        for f in ['test.py']:
            shutil.copyfile(os.path.join(self.tc.filesdir, f), self.tc.sdktestdir + f)

    def test_python_exists(self):
        self._run('which python')

    def test_python_stdout(self):
        output = self._run('python %s/test.py' % self.tc.sdktestdir)
        self.assertEqual(output.strip(), "the value of a is 0.01", msg="Incorrect output: %s" % output)

    def test_python_testfile(self):
        self._run('ls /tmp/testfile.python')

    @classmethod
    def tearDownClass(self):
        bb.utils.remove("%s/test.py" % self.tc.sdktestdir)
        bb.utils.remove("/tmp/testfile.python")
