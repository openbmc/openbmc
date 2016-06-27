import os
import tempfile

from oeqa.selftest.base import oeSelfTest
from oeqa.utils.commands import bitbake
from oeqa.utils import CommandError
from oeqa.utils.decorators import testcase

class LicenseTests(oeSelfTest):

    # Verify that changing a license file that has an absolute path causes
    # the license qa to fail due to a mismatched md5sum.
    @testcase(1197)
    def test_nonmatching_checksum(self):
        bitbake_cmd = '-c configure emptytest'
        error_msg = 'emptytest: The new md5 checksum is 8d777f385d3dfec8815d20f7496026dc'

        lic_file, lic_path = tempfile.mkstemp()
        os.close(lic_file)
        self.track_for_cleanup(lic_path)

        self.write_recipeinc('emptytest', 'INHIBIT_DEFAULT_DEPS = "1"')
        self.append_recipeinc('emptytest', 'LIC_FILES_CHKSUM = "file://%s;md5=d41d8cd98f00b204e9800998ecf8427e"' % lic_path)
        result = bitbake(bitbake_cmd)

        with open(lic_path, "w") as f:
            f.write("data")

        result = bitbake(bitbake_cmd, ignore_status=True)
        if error_msg not in result.output:
            raise AssertionError(result.output)
