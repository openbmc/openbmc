import os
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import skipUnlessPassed, testcase

def setUpModule():
    if not (oeRuntimeTest.hasPackage("dropbear") or oeRuntimeTest.hasPackage("openssh-sshd")):
        skipModule("No ssh package in image")

class ScpTest(oeRuntimeTest):

    @testcase(220)
    @skipUnlessPassed('test_ssh')
    def test_scp_file(self):
        test_log_dir = oeRuntimeTest.tc.d.getVar("TEST_LOG_DIR", True)
        test_file_path = os.path.join(test_log_dir, 'test_scp_file')
        with open(test_file_path, 'w') as test_scp_file:
            test_scp_file.seek(2 ** 22 - 1)
            test_scp_file.write(os.linesep)
        (status, output) = self.target.copy_to(test_file_path, '/tmp/test_scp_file')
        self.assertEqual(status, 0, msg = "File could not be copied. Output: %s" % output)
        (status, output) = self.target.run("ls -la /tmp/test_scp_file")
        self.assertEqual(status, 0, msg = "SCP test failed")
