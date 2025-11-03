#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_vars, get_bb_var, runqemu
import subprocess, os
import oe.path
import re

class VersionOrdering(OESelftestTestCase):
    # version1, version2, sort order
    tests = (
        ("1.0", "1.0", 0),
        ("1.0", "2.0", -1),
        ("2.0", "1.0", 1),
        ("2.0-rc", "2.0", 1),
        ("2.0~rc", "2.0", -1),
        ("1.2rc2", "1.2.0", -1)
        )

    @classmethod
    def setUpClass(cls):
        super().setUpClass()

        # Build the tools we need and populate a sysroot
        bitbake("dpkg-native opkg-native rpm-native python3-native")
        bitbake("build-sysroots -c build_native_sysroot")

        # Get the paths so we can point into the sysroot correctly
        vars = get_bb_vars(["STAGING_DIR", "BUILD_ARCH", "bindir_native", "libdir_native"])
        cls.staging = oe.path.join(vars["STAGING_DIR"], vars["BUILD_ARCH"])
        cls.bindir = oe.path.join(cls.staging, vars["bindir_native"])
        cls.libdir = oe.path.join(cls.staging, vars["libdir_native"])

    def setUpLocal(self):
        # Just for convenience
        self.staging = type(self).staging
        self.bindir = type(self).bindir
        self.libdir = type(self).libdir

    def test_dpkg(self):
        for ver1, ver2, sort in self.tests:
            op = { -1: "<<", 0: "=", 1: ">>" }[sort]
            status = subprocess.call((oe.path.join(self.bindir, "dpkg"), "--compare-versions", ver1, op, ver2))
            self.assertEqual(status, 0, "%s %s %s failed" % (ver1, op, ver2))

            # Now do it again but with incorrect operations
            op = { -1: ">>", 0: ">>", 1: "<<" }[sort]
            status = subprocess.call((oe.path.join(self.bindir, "dpkg"), "--compare-versions", ver1, op, ver2))
            self.assertNotEqual(status, 0, "%s %s %s failed" % (ver1, op, ver2))

            # Now do it again but with incorrect operations
            op = { -1: "=", 0: "<<", 1: "=" }[sort]
            status = subprocess.call((oe.path.join(self.bindir, "dpkg"), "--compare-versions", ver1, op, ver2))
            self.assertNotEqual(status, 0, "%s %s %s failed" % (ver1, op, ver2))

    def test_opkg(self):
        for ver1, ver2, sort in self.tests:
            op = { -1: "<<", 0: "=", 1: ">>" }[sort]
            status = subprocess.call((oe.path.join(self.bindir, "opkg"), "compare-versions", ver1, op, ver2))
            self.assertEqual(status, 0, "%s %s %s failed" % (ver1, op, ver2))

            # Now do it again but with incorrect operations
            op = { -1: ">>", 0: ">>", 1: "<<" }[sort]
            status = subprocess.call((oe.path.join(self.bindir, "opkg"), "compare-versions", ver1, op, ver2))
            self.assertNotEqual(status, 0, "%s %s %s failed" % (ver1, op, ver2))

            # Now do it again but with incorrect operations
            op = { -1: "=", 0: "<<", 1: "=" }[sort]
            status = subprocess.call((oe.path.join(self.bindir, "opkg"), "compare-versions", ver1, op, ver2))
            self.assertNotEqual(status, 0, "%s %s %s failed" % (ver1, op, ver2))

    def test_rpm(self):
        # Need to tell the Python bindings where to find its configuration
        env = os.environ.copy()
        env["RPM_CONFIGDIR"] = oe.path.join(self.libdir, "rpm")

        for ver1, ver2, sort in self.tests:
            # The only way to test rpm is via the Python module, so we need to
            # execute python3-native.  labelCompare returns -1/0/1 (like strcmp)
            # so add 100 and use that as the exit code.
            command = (oe.path.join(self.bindir, "python3-native", "python3"), "-c",
                       "import sys, rpm; v1=(None, \"%s\", None); v2=(None, \"%s\", None); sys.exit(rpm.labelCompare(v1, v2) + 100)" % (ver1, ver2))
            status = subprocess.call(command, env=env)
            self.assertIn(status, (99, 100, 101))
            self.assertEqual(status - 100, sort, "%s %s (%d) failed" % (ver1, ver2, sort))

class PackageTests(OESelftestTestCase):
    # Verify that a recipe cannot rename a package into an existing one
    def test_package_name_conflict(self):
        res = bitbake("packagenameconflict", ignore_status=True)
        self.assertNotEqual(res.status, 0)
        err = "package name already exists"
        self.assertTrue(err in res.output)

    # Verify that a recipe which sets up hardlink files has those preserved into split packages
    # Also test file sparseness is preserved
    def test_preserve_sparse_hardlinks(self):
        bitbake("selftest-hardlink -c package")

        dest = get_bb_var('PKGDEST', 'selftest-hardlink')
        bindir = get_bb_var('bindir', 'selftest-hardlink')
        libdir = get_bb_var('libdir', 'selftest-hardlink')
        libexecdir = get_bb_var('libexecdir', 'selftest-hardlink')

        def checkfiles():
            # Recipe creates 4 hardlinked files, there is a copy in package/ and a copy in packages-split/
            # so expect 8 in total.
            self.assertEqual(os.stat(dest + "/selftest-hardlink" + bindir + "/hello1").st_nlink, 8)
            self.assertEqual(os.stat(dest + "/selftest-hardlink" + libexecdir + "/hello3").st_nlink, 8)

            # Check dbg version
            # 2 items, a copy in both package/packages-split so 4
            self.assertEqual(os.stat(dest + "/selftest-hardlink-dbg" + bindir + "/.debug/hello1").st_nlink, 4)
            self.assertEqual(os.stat(dest + "/selftest-hardlink-dbg" + libexecdir + "/.debug/hello1").st_nlink, 4)

            # Even though the libexecdir name is 'hello3' or 'hello4', that isn't the debug target name
            self.assertEqual(os.path.exists(dest + "/selftest-hardlink-dbg" + libexecdir + "/.debug/hello3"), False)
            self.assertEqual(os.path.exists(dest + "/selftest-hardlink-dbg" + libexecdir + "/.debug/hello4"), False)

            # Check the staticdev libraries
            # 101 items, a copy in both package/packages-split so 202
            self.assertEqual(os.stat(dest + "/selftest-hardlink-staticdev" + libdir + "/libhello.a").st_nlink, 202)
            self.assertEqual(os.stat(dest + "/selftest-hardlink-staticdev" + libdir + "/libhello-25.a").st_nlink, 202)
            self.assertEqual(os.stat(dest + "/selftest-hardlink-staticdev" + libdir + "/libhello-50.a").st_nlink, 202)
            self.assertEqual(os.stat(dest + "/selftest-hardlink-staticdev" + libdir + "/libhello-75.a").st_nlink, 202)

            # Check static dbg
            # 101 items, a copy in both package/packages-split so 202
            self.assertEqual(os.stat(dest + "/selftest-hardlink-dbg" + libdir + "/.debug-static/libhello.a").st_nlink, 202)
            self.assertEqual(os.stat(dest + "/selftest-hardlink-dbg" + libdir + "/.debug-static/libhello-25.a").st_nlink, 202)
            self.assertEqual(os.stat(dest + "/selftest-hardlink-dbg" + libdir + "/.debug-static/libhello-50.a").st_nlink, 202)
            self.assertEqual(os.stat(dest + "/selftest-hardlink-dbg" + libdir + "/.debug-static/libhello-75.a").st_nlink, 202)

            # Test a sparse file remains sparse
            sparsestat = os.stat(dest + "/selftest-hardlink" + bindir + "/sparsetest")
            self.assertEqual(sparsestat.st_blocks, 0)
            self.assertEqual(sparsestat.st_size, 1048576)

        checkfiles()

        # Clean and reinstall so its now definitely from sstate, then retest.
        bitbake("selftest-hardlink -c clean")
        bitbake("selftest-hardlink -c package")

        checkfiles()

    # Verify gdb to read symbols from separated debug hardlink file correctly
    def test_gdb_hardlink_debug(self):
        features = 'IMAGE_INSTALL:append = " selftest-hardlink"\n'
        features += 'IMAGE_INSTALL:append = " selftest-hardlink-dbg"\n'
        features += 'IMAGE_INSTALL:append = " selftest-hardlink-gdb"\n'
        self.write_config(features)
        bitbake("core-image-minimal")

        def gdbtest(qemu, binary):
            """
            Check that gdb ``binary`` to read symbols from separated debug file
            """
            self.logger.info("gdbtest %s" % binary)
            status, output = qemu.run_serial('/usr/bin/gdb.sh %s' % binary, timeout=60)
            for l in output.split('\n'):
                # Check debugging symbols exists
                if '(no debugging symbols found)' in l:
                    self.logger.error("No debugging symbols found. GDB result:\n%s" % output)
                    return False

                # Check debugging symbols works correctly. Don't look for a
                # source file as optimisation can put the breakpoint inside
                # stdio.h.
                elif "Breakpoint 1 at" in l:
                    return True

            self.logger.error("GDB result:\n%d: %s", status, output)
            return False

        with runqemu('core-image-minimal') as qemu:
            for binary in ['/usr/bin/hello1',
                           '/usr/bin/hello2',
                           '/usr/libexec/hello3',
                           '/usr/libexec/hello4']:
                if not gdbtest(qemu, binary):
                    self.fail('GDB %s failed' % binary)

    def test_preserve_ownership(self):
        features = 'IMAGE_INSTALL:append = " selftest-chown"\n'
        self.write_config(features)
        bitbake("core-image-minimal")

        def check_ownership(qemu, expected_gid, expected_uid, path):
            self.logger.info("Check ownership of %s", path)
            status, output = qemu.run_serial('stat -c "%U %G" ' + path)
            self.assertEqual(status, 1, "stat failed: " + output)
            try:
                uid, gid = output.split()
                self.assertEqual(uid, expected_uid)
                self.assertEqual(gid, expected_gid)
            except ValueError:
                self.fail("Cannot parse output: " + output)

        sysconfdir = get_bb_var('sysconfdir', 'selftest-chown')
        with runqemu('core-image-minimal') as qemu:
            for path in [ sysconfdir + "/selftest-chown/file",
                          sysconfdir + "/selftest-chown/dir",
                          sysconfdir + "/selftest-chown/symlink",
                          sysconfdir + "/selftest-chown/fifotest/fifo"]:
                check_ownership(qemu, "test", "test", path)
