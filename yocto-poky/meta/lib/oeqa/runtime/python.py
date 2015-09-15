import unittest
import os
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    if not oeRuntimeTest.hasPackage("python"):
        skipModule("No python package in the image")


class PythonTest(oeRuntimeTest):

    @classmethod
    def setUpClass(self):
        oeRuntimeTest.tc.target.copy_to(os.path.join(oeRuntimeTest.tc.filesdir, "test.py"), "/tmp/test.py")

    @testcase(1145)
    def test_python_exists(self):
        (status, output) = self.target.run('which python')
        self.assertEqual(status, 0, msg="Python binary not in PATH or not on target.")

    @testcase(965)
    def test_python_stdout(self):
        (status, output) = self.target.run('python /tmp/test.py')
        self.assertEqual(status, 0, msg="Exit status was not 0. Output: %s" % output)
        self.assertEqual(output, "the value of a is 0.01", msg="Incorrect output: %s" % output)

    @testcase(1146)
    def test_python_testfile(self):
        (status, output) = self.target.run('ls /tmp/testfile.python')
        self.assertEqual(status, 0, msg="Python test file generate failed.")

    @classmethod
    def tearDownClass(self):
        oeRuntimeTest.tc.target.run("rm /tmp/test.py /tmp/testfile.python")
