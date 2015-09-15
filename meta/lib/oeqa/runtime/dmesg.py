import unittest
from oeqa.oetest import oeRuntimeTest
from oeqa.utils.decorators import *


class DmesgTest(oeRuntimeTest):

    @testcase(215)
    @skipUnlessPassed('test_ssh')
    def test_dmesg(self):
        (status, output) = self.target.run('dmesg | grep -v mmci-pl18x | grep -v "error changing net interface name" | grep -iv "dma timeout" | grep -v usbhid | grep -i error')
        self.assertEqual(status, 1, msg = "Error messages in dmesg log: %s" % output)
