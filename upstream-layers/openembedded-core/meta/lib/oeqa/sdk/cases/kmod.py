#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import subprocess
import tempfile

from oeqa.sdk.case import OESDKTestCase
from oeqa.sdkext.context import OESDKExtTestContext
from oeqa.utils.subprocesstweak import errors_have_output
errors_have_output()

class KernelModuleTest(OESDKTestCase):
    """
    Test that out-of-tree kernel modules build.
    """
    def test_cryptodev(self):
        if isinstance(self.tc, OESDKExtTestContext):
            self.skipTest(f"{self.id()} does not support eSDK (https://bugzilla.yoctoproject.org/show_bug.cgi?id=15850)")

        from oe.utils import parallel_make_value
        pmv = parallel_make_value((self.td.get('PARALLEL_MAKE') or '').split())
        parallel_make = "-j %d" % (pmv) if pmv else ""

        self.ensure_target_package("kernel-devsrc")
        # These targets need to be built before kernel modules can be built.
        self._run("make %s -C $OECORE_TARGET_SYSROOT/usr/src/kernel prepare scripts" % (parallel_make))

        with tempfile.TemporaryDirectory(prefix="cryptodev", dir=self.tc.sdk_dir) as testdir:
            git_url = "https://github.com/cryptodev-linux/cryptodev-linux"
            # This is a knnown-good commit post-1.13 that builds with kernel 6.7+
            git_sha = "bb8bc7cf60d2c0b097c8b3b0e807f805b577a53f"

            sourcedir = os.path.join(testdir, "cryptodev-linux")
            subprocess.check_output(["git", "clone", git_url, sourcedir], stderr=subprocess.STDOUT)
            self.assertTrue(os.path.isdir(sourcedir))
            subprocess.check_output(["git", "-C", sourcedir, "checkout", git_sha], stderr=subprocess.STDOUT)

            self._run("make -C %s V=1 KERNEL_DIR=$OECORE_TARGET_SYSROOT/usr/src/kernel" % sourcedir)
            self.check_elf(os.path.join(sourcedir, "cryptodev.ko"))
