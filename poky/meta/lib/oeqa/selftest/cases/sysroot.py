#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import uuid

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake

class SysrootTests(OESelftestTestCase):
    def test_sysroot_cleanup(self):
        """
        Build sysroot test which depends on virtual/sysroot-test for one machine,
        switch machine, switch provider of virtual/sysroot-test and check that the
        sysroot is correctly cleaned up. The files in the two providers overlap
        so can cause errors if the sysroot code doesn't function correctly.
        Yes, sysroot-test should be machine specific really to avoid this, however
        the sysroot cleanup should also work [YOCTO #13702]. 
        """

        uuid1 = uuid.uuid4()
        uuid2 = uuid.uuid4()

        self.write_config("""
PREFERRED_PROVIDER_virtual/sysroot-test = "sysroot-test-arch1"
MACHINE = "qemux86"
TESTSTRING:pn-sysroot-test-arch1 = "%s"
TESTSTRING:pn-sysroot-test-arch2 = "%s"
""" % (uuid1, uuid2))
        bitbake("sysroot-test")
        self.write_config("""
PREFERRED_PROVIDER_virtual/sysroot-test = "sysroot-test-arch2"
MACHINE = "qemux86copy"
TESTSTRING:pn-sysroot-test-arch1 = "%s"
TESTSTRING:pn-sysroot-test-arch2 = "%s"
""" % (uuid1, uuid2))
        bitbake("sysroot-test")

    def test_sysroot_max_shebang(self):
        """
        Summary:   Check max shebang triggers. To confirm [YOCTO #11053] is closed.
        Expected:  Fail when a shebang bigger than the max shebang-size is reached.
        Author:    Paulo Neves <ptsneves@gmail.com>
        """
        expected = "maximum shebang size exceeded, the maximum size is 128. [shebang-size]"
        res = bitbake("sysroot-shebang-test-native -c populate_sysroot", ignore_status=True)
        self.assertTrue(expected in res.output, msg=res.output)
        self.assertTrue(res.status != 0)

    def test_sysroot_la(self):
        """
        Summary:   Check that workdir paths are not contained in .la files.
        Expected:  Fail when a workdir path is found in the file content.
        Author:    Paulo Neves <ptsneves@gmail.com>
        """
        expected = "la-test.la failed sanity test (workdir) in path"

        res = bitbake("sysroot-la-test -c populate_sysroot", ignore_status=True)
        self.assertTrue(expected in res.output, msg=res.output)
        self.assertTrue('[la]' in res.output, msg=res.output)
        self.assertTrue(res.status != 0)

        res = bitbake("sysroot-la-test-native -c populate_sysroot", ignore_status=True)
        self.assertTrue(expected in res.output, msg=res.output)
        self.assertTrue('[la]' in res.output, msg=res.output)
        self.assertTrue(res.status != 0)

    def test_sysroot_pkgconfig(self):
        """
        Summary:   Check that tmpdir paths are not contained in .pc files.
        Expected:  Fail when a tmpdir path is found in the file content.
        Author:    Paulo Neves <ptsneves@gmail.com>
        """
        expected = "test.pc failed sanity test (tmpdir) in path"

        res = bitbake("sysroot-pc-test -c populate_sysroot", ignore_status=True)
        self.assertTrue('[pkgconfig]' in res.output, msg=res.output)
        self.assertTrue(expected in res.output, msg=res.output)
        self.assertTrue(res.status != 0)

        res = bitbake("sysroot-pc-test-native -c populate_sysroot", ignore_status=True)
        self.assertTrue(expected in res.output, msg=res.output)
        self.assertTrue('[pkgconfig]' in res.output, msg=res.output)
        self.assertTrue(res.status != 0)
