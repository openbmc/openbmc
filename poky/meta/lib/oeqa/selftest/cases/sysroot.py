#
# SPDX-License-Identifier: MIT
#

import uuid

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import  bitbake

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
TESTSTRING_pn-sysroot-test-arch1 = "%s"
TESTSTRING_pn-sysroot-test-arch2 = "%s"
""" % (uuid1, uuid2))
        bitbake("sysroot-test")
        self.write_config("""
PREFERRED_PROVIDER_virtual/sysroot-test = "sysroot-test-arch2"
MACHINE = "qemux86copy"
TESTSTRING_pn-sysroot-test-arch1 = "%s"
TESTSTRING_pn-sysroot-test-arch2 = "%s"
""" % (uuid1, uuid2))
        bitbake("sysroot-test")
