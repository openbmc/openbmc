import unittest
from oeqa.oetest import oeRuntimeTest
from oeqa.utils.qemutinyrunner import *

class QemuTinyTest(oeRuntimeTest):

    def test_boot_tiny(self):
        (status, output) = self.target.run_serial('uname -a')
        self.assertTrue("yocto-tiny" in output, msg="Cannot detect poky tiny boot!")