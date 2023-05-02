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
from oeqa.utils.commands import bitbake, get_bb_var, runCmd


class Minidebuginfo(OESelftestTestCase):
    def test_minidebuginfo(self):
        target_sys = get_bb_var("TARGET_SYS")
        binutils = "binutils-cross-{}".format(get_bb_var("TARGET_ARCH"))

        self.write_config("""
PACKAGE_MINIDEBUGINFO = "1"
IMAGE_FSTYPES = "tar.bz2"
""")
        bitbake("core-image-minimal {}:do_addto_recipe_sysroot".format(binutils))

        deploy_dir = get_bb_var("DEPLOY_DIR_IMAGE")
        native_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", binutils)
        readelf = get_bb_var("READELF", "core-image-minimal")

        # add usr/bin/${TARGET_SYS} to PATH
        env = os.environ.copy()
        paths = [os.path.join(native_sysroot, "usr", "bin", target_sys)]
        paths += env["PATH"].split(":")
        env["PATH"] = ":".join(paths)

        # confirm that executables and shared libraries contain an ELF section
        # ".gnu_debugdata" which stores minidebuginfo.
        with tempfile.TemporaryDirectory(prefix = "unpackfs-") as unpackedfs:
            filename = os.path.join(deploy_dir, "core-image-minimal-{}.tar.bz2".format(self.td["MACHINE"]))
            shutil.unpack_archive(filename, unpackedfs)

            r = runCmd([readelf, "-W", "-S", os.path.join(unpackedfs, "bin", "busybox")],
                    native_sysroot = native_sysroot, env = env)
            self.assertIn(".gnu_debugdata", r.output)

            r = runCmd([readelf, "-W", "-S", os.path.join(unpackedfs, "lib", "libc.so.6")],
                    native_sysroot = native_sysroot, env = env)
            self.assertIn(".gnu_debugdata", r.output)

