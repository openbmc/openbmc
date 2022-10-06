#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
import os
import socketserver
import subprocess

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_var, runqemu


class Debuginfod(OESelftestTestCase):
    def test_debuginfod(self):
        self.write_config(
            """
DISTRO_FEATURES:append = " debuginfod"
CORE_IMAGE_EXTRA_INSTALL += "elfutils"
        """
        )
        bitbake("core-image-minimal elfutils-native:do_addto_recipe_sysroot")

        native_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", "elfutils-native")
        cmd = [
            os.path.join(native_sysroot, "usr", "bin", "debuginfod"),
            "--verbose",
            "--database=:memory:",
            get_bb_var("DEPLOY_DIR"),
        ]
        for format in get_bb_var("PACKAGE_CLASSES").split():
            if format == "package_deb":
                cmd.append("--scan-deb-dir")
            elif format == "package_ipk":
                cmd.append("--scan-deb-dir")
            elif format == "package_rpm":
                cmd.append("--scan-rpm-dir")
        # Find a free port
        with socketserver.TCPServer(("localhost", 0), None) as s:
            port = s.server_address[1]
            cmd.append("--port=%d" % port)

        try:
            debuginfod = subprocess.Popen(cmd)

            with runqemu("core-image-minimal", runqemuparams="nographic") as qemu:
                cmd = (
                    "DEBUGINFOD_URLS=http://%s:%d/ debuginfod-find debuginfo /usr/bin/debuginfod"
                    % (qemu.server_ip, port)
                )
                status, output = qemu.run_serial(cmd)
                # This should be more comprehensive
                self.assertIn("/.cache/debuginfod_client/", output)
        finally:
            debuginfod.kill()
