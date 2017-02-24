import os

from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.commands import runCmd
from oeqa.utils.decorators import *

class Selftest(oeRuntimeTest):

    @skipUnlessPassed("test_ssh")
    @tag("selftest_package_install")
    def test_install_package(self):
        """
        Summary: Check basic package installation functionality.
        Expected: 1. Before the test socat must be installed using scp.
                  2. After the test socat must be unistalled using ssh.
                     This can't be checked in this test.
        Product: oe-core
        Author: Mariano Lopez <mariano.lopez@intel.com>
        """

        (status, output) = self.target.run("socat -V")
        self.assertEqual(status, 0, msg="socat is not installed")

    @skipUnlessPassed("test_install_package")
    @tag("selftest_package_install")
    def test_verify_unistall(self):
        """
        Summary: Check basic package installation functionality.
        Expected: 1. test_install_package must unistall socat.
                     This test is just to verify that.
        Product: oe-core
        Author: Mariano Lopez <mariano.lopez@intel.com>
        """

        (status, output) = self.target.run("socat -V")
        self.assertNotEqual(status, 0, msg="socat is still installed")

    @tag("selftest_sdk")
    def test_sdk(self):

        result = runCmd("env -0")
        sdk_path = search_sdk_path(result.output)
        self.assertTrue(sdk_path, msg="Can't find SDK path")

        tar_cmd = os.path.join(sdk_path, "tar")
        result = runCmd("%s --help" % tar_cmd)

def search_sdk_path(env):
    for line in env.split("\0"):
        (key, _, value) = line.partition("=")
        if key == "PATH":
            for path in value.split(":"):
                if "pokysdk" in path:
                    return path
    return ""
