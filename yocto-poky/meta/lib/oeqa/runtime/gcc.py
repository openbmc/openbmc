import unittest
import os
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    if not oeRuntimeTest.hasFeature("tools-sdk"):
        skipModule("Image doesn't have tools-sdk in IMAGE_FEATURES")


class GccCompileTest(oeRuntimeTest):

    @classmethod
    def setUpClass(self):
        oeRuntimeTest.tc.target.copy_to(os.path.join(oeRuntimeTest.tc.filesdir, "test.c"), "/tmp/test.c")
        oeRuntimeTest.tc.target.copy_to(os.path.join(oeRuntimeTest.tc.filesdir, "testmakefile"), "/tmp/testmakefile")
        oeRuntimeTest.tc.target.copy_to(os.path.join(oeRuntimeTest.tc.filesdir, "test.cpp"), "/tmp/test.cpp")

    @testcase(203)
    def test_gcc_compile(self):
        (status, output) = self.target.run('gcc /tmp/test.c -o /tmp/test -lm')
        self.assertEqual(status, 0, msg="gcc compile failed, output: %s" % output)
        (status, output) = self.target.run('/tmp/test')
        self.assertEqual(status, 0, msg="running compiled file failed, output %s" % output)

    @testcase(200)
    def test_gpp_compile(self):
        (status, output) = self.target.run('g++ /tmp/test.c -o /tmp/test -lm')
        self.assertEqual(status, 0, msg="g++ compile failed, output: %s" % output)
        (status, output) = self.target.run('/tmp/test')
        self.assertEqual(status, 0, msg="running compiled file failed, output %s" % output)

    @testcase(1142)
    def test_gpp2_compile(self):
        (status, output) = self.target.run('g++ /tmp/test.cpp -o /tmp/test -lm')
        self.assertEqual(status, 0, msg="g++ compile failed, output: %s" % output)
        (status, output) = self.target.run('/tmp/test')
        self.assertEqual(status, 0, msg="running compiled file failed, output %s" % output)

    @testcase(204)
    def test_make(self):
        (status, output) = self.target.run('cd /tmp; make -f testmakefile')
        self.assertEqual(status, 0, msg="running make failed, output %s" % output)

    @classmethod
    def tearDownClass(self):
        oeRuntimeTest.tc.target.run("rm /tmp/test.c /tmp/test.o /tmp/test /tmp/testmakefile")
