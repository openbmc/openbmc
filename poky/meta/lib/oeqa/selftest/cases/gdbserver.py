#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
import os
import time
import tempfile
import shutil
import concurrent.futures

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_var, get_bb_vars , runqemu, runCmd

class GdbServerTest(OESelftestTestCase):
    def test_gdb_server(self):
        target_arch = self.td["TARGET_ARCH"]
        target_sys = self.td["TARGET_SYS"]

        features = """
IMAGE_GEN_DEBUGFS = "1"
IMAGE_FSTYPES_DEBUGFS = "tar.bz2"
CORE_IMAGE_EXTRA_INSTALL = "gdbserver"
        """
        self.write_config(features)

        gdb_recipe = "gdb-cross-" + target_arch
        gdb_binary = target_sys + "-gdb"

        bitbake("core-image-minimal %s:do_addto_recipe_sysroot" % gdb_recipe)

        native_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", gdb_recipe)
        r = runCmd("%s --version" % gdb_binary, native_sysroot=native_sysroot, target_sys=target_sys)
        self.assertEqual(r.status, 0)
        self.assertIn("GNU gdb", r.output)
        image = 'core-image-minimal'
        bb_vars = get_bb_vars(['DEPLOY_DIR_IMAGE', 'IMAGE_LINK_NAME'], image)

        with tempfile.TemporaryDirectory(prefix="debugfs-") as debugfs:
            filename = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'], "%s-dbg.tar.bz2" % bb_vars['IMAGE_LINK_NAME'])
            shutil.unpack_archive(filename, debugfs)
            filename = os.path.join(bb_vars['DEPLOY_DIR_IMAGE'], "%s.tar.bz2" % bb_vars['IMAGE_LINK_NAME'])
            shutil.unpack_archive(filename, debugfs)

            with runqemu("core-image-minimal", runqemuparams="nographic") as qemu:
                status, output = qemu.run_serial("kmod --help")
                self.assertIn("modprobe", output)

                with concurrent.futures.ThreadPoolExecutor(max_workers=1) as executor:
                    def run_gdb():
                        for _ in range(5):
                            time.sleep(2)
                            cmd = "%s --batch -ex 'set sysroot %s' -ex \"target extended-remote %s:9999\" -ex \"info line kmod_help\"" % (gdb_binary, debugfs, qemu.ip)
                            self.logger.warning("starting gdb %s" % cmd)
                            r = runCmd(cmd, native_sysroot=native_sysroot, target_sys=target_sys)
                            self.assertEqual(0, r.status)
                            line_re = r"Line \d+ of \"/usr/src/debug/kmod/.*/tools/kmod.c\" starts at address 0x[0-9A-Fa-f]+ <kmod_help>"
                            self.assertRegex(r.output, line_re)
                            break
                        else:
                            self.fail("Timed out connecting to gdb")
                    future = executor.submit(run_gdb)

                    status, output = qemu.run_serial("gdbserver --once :9999 kmod --help")
                    self.assertEqual(status, 1)
                    # The future either returns None, or raises an exception
                    future.result()
