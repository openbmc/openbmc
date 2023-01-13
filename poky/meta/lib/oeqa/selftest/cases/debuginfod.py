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

    def wait_for_debuginfod(self, port):
        """
        debuginfod takes time to scan the packages and requesting too early may
        result in a test failure if the right packages haven't been scanned yet.

        Request the metrics endpoint periodically and wait for there to be no
        busy scanning threads.

        Returns True if debuginfod is ready, False if we timed out
        """
        import time, urllib

        # Wait a minute
        countdown = 6
        delay = 10

        while countdown:
            time.sleep(delay)
            try:
                with urllib.request.urlopen("http://localhost:%d/metrics" % port) as f:
                    lines = f.read().decode("ascii").splitlines()
                    if "thread_busy{role=\"scan\"} 0" in lines:
                        return True
            except urllib.error.URLError as e:
                self.logger.error(e)
            countdown -= 1
        return False


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
            # In-memory database, this is a one-shot test
            "--database=:memory:",
            # Don't use all the host cores
            "--concurrency=8",
            "--connection-pool=8",
            # Disable rescanning, this is a one-shot test
            "--rescan-time=0",
            "--groom-time=0",
            get_bb_var("DEPLOY_DIR"),
        ]

        format = get_bb_var("PACKAGE_CLASSES").split()[0]
        if format == "package_deb":
            cmd.append("--scan-deb-dir")
        elif format == "package_ipk":
            cmd.append("--scan-deb-dir")
        elif format == "package_rpm":
            cmd.append("--scan-rpm-dir")
        else:
            self.fail("Unknown package class %s" % format)

        # Find a free port
        with socketserver.TCPServer(("localhost", 0), None) as s:
            port = s.server_address[1]
            cmd.append("--port=%d" % port)

        try:
            # Remove DEBUGINFOD_URLS from the environment so we don't try
            # looking in the distro debuginfod
            env = os.environ.copy()
            if "DEBUGINFOD_URLS" in env:
                del env["DEBUGINFOD_URLS"]

            self.logger.info(f"Starting server {cmd}")
            debuginfod = subprocess.Popen(cmd, env=env)

            with runqemu("core-image-minimal", runqemuparams="nographic") as qemu:
                self.assertTrue(self.wait_for_debuginfod(port))

                cmd = (
                    "DEBUGINFOD_URLS=http://%s:%d/ debuginfod-find debuginfo /usr/bin/debuginfod"
                    % (qemu.server_ip, port)
                )
                self.logger.info(f"Starting client {cmd}")
                status, output = qemu.run_serial(cmd)
                # This should be more comprehensive
                self.assertIn("/.cache/debuginfod_client/", output)
        finally:
            debuginfod.kill()
