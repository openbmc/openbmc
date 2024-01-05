#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
import os
import subprocess
import tempfile
import shutil

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_var, get_bb_vars, runCmd


class Minidebuginfo(OESelftestTestCase):
    def test_minidebuginfo(self):
        target_sys = get_bb_var("TARGET_SYS")
        binutils = "binutils-cross-{}".format(get_bb_var("TARGET_ARCH"))

        image = 'core-image-minimal'
        bb_vars = get_bb_vars(['DEPLOY_DIR_IMAGE', 'IMAGE_LINK_NAME', 'READELF'], image)

        self.write_config("""
DISTRO_FEATURES:append = " minidebuginfo"
IMAGE_FSTYPES = "tar.bz2"
""")
        bitbake("{} {}:do_addto_recipe_sysroot".format(image, binutils))

        native_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", binutils)

        # confirm that executables and shared libraries contain an ELF section
        # ".gnu_debugdata" which stores minidebuginfo.
        with tempfile.TemporaryDirectory(prefix = "unpackfs-") as unpackedfs:
            filename = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'], "{}.tar.bz2".format(bb_vars['IMAGE_LINK_NAME']))
            shutil.unpack_archive(filename, unpackedfs)

            r = runCmd([bb_vars['READELF'], "-W", "-S", os.path.join(unpackedfs, "bin", "busybox")],
                    native_sysroot = native_sysroot, target_sys = target_sys)
            self.assertIn(".gnu_debugdata", r.output)

            r = runCmd([bb_vars['READELF'], "-W", "-S", os.path.join(unpackedfs, "lib", "libc.so.6")],
                    native_sysroot = native_sysroot, target_sys = target_sys)
            self.assertIn(".gnu_debugdata", r.output)

