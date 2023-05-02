#
# SPDX-License-Identifier: MIT
#

import os
import tempfile
import urllib

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake

class LicenseTests(OESelftestTestCase):

    def test_checksum_with_space(self):
        bitbake_cmd = '-c populate_lic emptytest'

        lic_file, lic_path = tempfile.mkstemp(" -afterspace")
        os.close(lic_file)
        #self.track_for_cleanup(lic_path)

        self.write_config("INHERIT:remove = \"report-error\"")

        self.write_recipeinc('emptytest', """
INHIBIT_DEFAULT_DEPS = "1"
LIC_FILES_CHKSUM = "file://%s;md5=d41d8cd98f00b204e9800998ecf8427e"
SRC_URI = "file://%s;md5=d41d8cd98f00b204e9800998ecf8427e"
""" % (urllib.parse.quote(lic_path), urllib.parse.quote(lic_path)))
        result = bitbake(bitbake_cmd)
        self.delete_recipeinc('emptytest')


    # Verify that changing a license file that has an absolute path causes
    # the license qa to fail due to a mismatched md5sum.
    def test_nonmatching_checksum(self):
        bitbake_cmd = '-c populate_lic emptytest'
        error_msg = 'emptytest: The new md5 checksum is 8d777f385d3dfec8815d20f7496026dc'

        lic_file, lic_path = tempfile.mkstemp()
        os.close(lic_file)
        self.track_for_cleanup(lic_path)

        self.write_config("INHERIT:remove = \"report-error\"")

        self.write_recipeinc('emptytest', """
INHIBIT_DEFAULT_DEPS = "1"
LIC_FILES_CHKSUM = "file://%s;md5=d41d8cd98f00b204e9800998ecf8427e"
SRC_URI = "file://%s;md5=d41d8cd98f00b204e9800998ecf8427e"
""" % (lic_path, lic_path))
        result = bitbake(bitbake_cmd)

        with open(lic_path, "w") as f:
            f.write("data")

        result = bitbake(bitbake_cmd, ignore_status=True)
        self.delete_recipeinc('emptytest')
        if error_msg not in result.output:
            raise AssertionError(result.output)
