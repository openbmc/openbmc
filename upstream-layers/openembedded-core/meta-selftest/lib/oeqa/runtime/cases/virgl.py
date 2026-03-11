from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
import subprocess
import oe.lsb

class VirglTest(OERuntimeTestCase):

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_kernel_driver(self):
        status, output = self.target.run('dmesg|grep virgl')
        self.assertEqual(status, 0, "Checking for virgl driver in dmesg returned non-zero: %d\n%s" % (status, output))
        self.assertIn("features: +virgl", output, "virgl acceleration seems to be disabled:\n%s" %(output))

    @OETestDepends(['virgl.VirglTest.test_kernel_driver'])
    def test_kmscube(self):
        status, output = self.target.run('kmscube')
        self.assertEqual(status, 0, "kmscube exited with non-zero status %d and output:\n%s" %(status, output))
        self.assertIn('renderer: "virgl', output, "kmscube does not seem to use virgl:\n%s" %(output))
