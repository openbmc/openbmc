#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
import os
import socketserver
import subprocess
import time
import urllib
import pathlib

from oeqa.core.decorator import OETestTag
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_var, runqemu


class Debuginfod(OESelftestTestCase):

    def wait_for_debuginfod(self, port):
        """
        debuginfod takes time to scan the packages and requesting too early may
        result in a test failure if the right packages haven't been scanned yet.

        Request the metrics endpoint periodically and wait for there to be no
        busy scanning threads.

        Returns if debuginfod is ready, raises an exception if not within the
        timeout.
        """

        # Wait two minutes
        countdown = 24
        delay = 5
        latest = None

        while countdown:
            self.logger.info("waiting...")
            time.sleep(delay)

            self.logger.info("polling server")
            if self.debuginfod.poll():
                self.logger.info("server dead")
                self.debuginfod.communicate()
                self.fail("debuginfod terminated unexpectedly")
            self.logger.info("server alive")

            try:
                with urllib.request.urlopen("http://localhost:%d/metrics" % port, timeout=10) as f:
                    for line in f.read().decode("ascii").splitlines():
                        key, value = line.rsplit(" ", 1)
                        if key == "thread_busy{role=\"scan\"}":
                            latest = int(value)
                            self.logger.info("Waiting for %d scan jobs to finish" % latest)
                            if latest == 0:
                                return
            except urllib.error.URLError as e:
                # TODO: how to catch just timeouts?
                self.logger.error(e)

            countdown -= 1

        raise TimeoutError("Cannot connect debuginfod, still %d scan jobs running" % latest)

    def start_debuginfod(self, feed_dir):
        # We assume that the caller has already bitbake'd elfutils-native:do_addto_recipe_sysroot

        # Save some useful paths for later
        native_sysroot = pathlib.Path(get_bb_var("RECIPE_SYSROOT_NATIVE", "elfutils-native"))
        native_bindir = native_sysroot / "usr" / "bin"
        self.debuginfod = native_bindir / "debuginfod"
        self.debuginfod_find = native_bindir / "debuginfod-find"

        cmd = [
            self.debuginfod,
            "--verbose",
            # In-memory database, this is a one-shot test
            "--database=:memory:",
            # Don't use all the host cores
            "--concurrency=8",
            "--connection-pool=8",
            # Disable rescanning, this is a one-shot test
            "--rescan-time=0",
            "--groom-time=0",
            feed_dir,
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

        # Find a free port. Racey but the window is small.
        with socketserver.TCPServer(("localhost", 0), None) as s:
            self.port = s.server_address[1]
            cmd.append("--port=%d" % self.port)

        self.logger.info(f"Starting server {cmd}")
        self.debuginfod = subprocess.Popen(cmd, env={})
        self.wait_for_debuginfod(self.port)


    def test_debuginfod_native(self):
        """
        Test debuginfod outside of qemu, by building a package and looking up a
        binary's debuginfo using elfutils-native.
        """

        self.write_config("""
TMPDIR = "${TOPDIR}/tmp-debuginfod"
DISTRO_FEATURES:append = " debuginfod"
INHERIT += "localpkgfeed"
""")
        bitbake("elfutils-native:do_addto_recipe_sysroot xz xz:do_package xz:do_localpkgfeed")

        try:
            self.start_debuginfod(get_bb_var("LOCALPKGFEED_DIR", "xz"))

            env = os.environ.copy()
            env["DEBUGINFOD_URLS"] = "http://localhost:%d/" % self.port

            pkgs = pathlib.Path(get_bb_var("PKGDEST", "xz"))
            cmd = (self.debuginfod_find, "debuginfo", pkgs / "xz" / "usr" / "bin" / "xz.xz")
            self.logger.info(f"Starting client {cmd}")
            output = subprocess.check_output(cmd, env=env, text=True)
            # This should be more comprehensive
            self.assertIn("/.cache/debuginfod_client/", output)
        finally:
            self.debuginfod.kill()

    @OETestTag("runqemu")
    def test_debuginfod_qemu(self):
        """
        Test debuginfod-find inside a qemu, talking to a debuginfod on the host.
        """

        self.write_config("""
TMPDIR = "${TOPDIR}/tmp-debuginfod"
DISTRO_FEATURES:append = " debuginfod"
INHERIT += "localpkgfeed"
CORE_IMAGE_EXTRA_INSTALL += "elfutils xz"
        """)
        bitbake("core-image-minimal elfutils-native:do_addto_recipe_sysroot xz:do_localpkgfeed")

        try:
            self.start_debuginfod(get_bb_var("LOCALPKGFEED_DIR", "xz"))

            with runqemu("core-image-minimal", runqemuparams="nographic") as qemu:
                cmd = "DEBUGINFOD_URLS=http://%s:%d/ debuginfod-find debuginfo /usr/bin/xz" % (qemu.server_ip, self.port)
                self.logger.info(f"Starting client {cmd}")
                status, output = qemu.run_serial(cmd)
                # This should be more comprehensive
                self.assertIn("/.cache/debuginfod_client/", output)
        finally:
            self.debuginfod.kill()
