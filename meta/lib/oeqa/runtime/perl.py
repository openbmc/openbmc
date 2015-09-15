import unittest
import os
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    if not oeRuntimeTest.hasPackage("perl"):
        skipModule("No perl package in the image")


class PerlTest(oeRuntimeTest):

    @classmethod
    def setUpClass(self):
        oeRuntimeTest.tc.target.copy_to(os.path.join(oeRuntimeTest.tc.filesdir, "test.pl"), "/tmp/test.pl")

    @testcase(1141)
    def test_perl_exists(self):
        (status, output) = self.target.run('which perl')
        self.assertEqual(status, 0, msg="Perl binary not in PATH or not on target.")

    @testcase(208)
    def test_perl_works(self):
        (status, output) = self.target.run('perl /tmp/test.pl')
        self.assertEqual(status, 0, msg="Exit status was not 0. Output: %s" % output)
        self.assertEqual(output, "the value of a is 0.01", msg="Incorrect output: %s" % output)

    @classmethod
    def tearDownClass(self):
        oeRuntimeTest.tc.target.run("rm /tmp/test.pl")
