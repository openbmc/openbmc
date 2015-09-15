import unittest
import os
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    if not oeRuntimeTest.hasFeature("tools-sdk"):
        skipModule("Image doesn't have tools-sdk in IMAGE_FEATURES")


class KernelModuleTest(oeRuntimeTest):

    def setUp(self):
        self.target.copy_to(os.path.join(oeRuntimeTest.tc.filesdir, "hellomod.c"), "/tmp/hellomod.c")
        self.target.copy_to(os.path.join(oeRuntimeTest.tc.filesdir, "hellomod_makefile"), "/tmp/Makefile")

    @testcase('316')
    @skipUnlessPassed('test_ssh')
    @skipUnlessPassed('test_gcc_compile')
    def test_kernel_module(self):
        cmds = [
            'cd /usr/src/kernel && make scripts',
            'cd /tmp && make',
            'cd /tmp && insmod hellomod.ko',
            'lsmod | grep hellomod',
            'dmesg | grep Hello',
            'rmmod hellomod', 'dmesg | grep "Cleaning up hellomod"'
            ]
        for cmd in cmds:
            (status, output) = self.target.run(cmd, 900)
            self.assertEqual(status, 0, msg="\n".join([cmd, output]))

    def tearDown(self):
        self.target.run('rm -f /tmp/Makefile /tmp/hellomod.c')
