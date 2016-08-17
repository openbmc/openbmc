# This test should cover https://bugzilla.yoctoproject.org/tr_show_case.cgi?case_id=284 testcase
# Note that the image under test must have meta-skeleton layer in bblayers and IMAGE_INSTALL_append = " service" in local.conf

import unittest
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *

def setUpModule():
    if not oeRuntimeTest.hasPackage("service"):
        skipModule("No service package in image")


class SkeletonBasicTest(oeRuntimeTest):

    @skipUnlessPassed('test_ssh')
    @unittest.skipIf("systemd" == oeRuntimeTest.tc.d.getVar("VIRTUAL-RUNTIME_init_manager", False), "Not appropiate for systemd image")
    def test_skeleton_availability(self):
        (status, output) = self.target.run('ls /etc/init.d/skeleton')
        self.assertEqual(status, 0, msg = "skeleton init script not found. Output:\n%s " % output)
        (status, output) =  self.target.run('ls /usr/sbin/skeleton-test')
        self.assertEqual(status, 0, msg = "skeleton-test not found. Output:\n%s" % output)

    @testcase(284)
    @skipUnlessPassed('test_skeleton_availability')
    @unittest.skipIf("systemd" == oeRuntimeTest.tc.d.getVar("VIRTUAL-RUNTIME_init_manager", False), "Not appropiate for systemd image")
    def test_skeleton_script(self):
        output1 = self.target.run("/etc/init.d/skeleton start")[1]
        (status, output2) = self.target.run(oeRuntimeTest.pscmd + ' | grep [s]keleton-test')
        self.assertEqual(status, 0, msg = "Skeleton script could not be started:\n%s\n%s" % (output1, output2))
