#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_vars, get_bb_var, runqemu
import stat
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
    # Verify that a recipe which sets up hardlink files has those preserved into split packages
    # Also test file sparseness is preserved
    def test_preserve_sparse_hardlinks(self):
        bitbake("selftest-hardlink -c package")

        dest = get_bb_var('PKGDEST', 'selftest-hardlink')
        bindir = get_bb_var('bindir', 'selftest-hardlink')

        def checkfiles():
            # Recipe creates 4 hardlinked files, there is a copy in package/ and a copy in packages-split/
            # so expect 8 in total.
            self.assertEqual(os.stat(dest + "/selftest-hardlink" + bindir + "/hello1").st_nlink, 8)

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
        features = 'IMAGE_INSTALL_append = " selftest-hardlink"\n'
        features += 'IMAGE_INSTALL_append = " selftest-hardlink-dbg"\n'
        features += 'IMAGE_INSTALL_append = " selftest-hardlink-gdb"\n'
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

                # Check debugging symbols works correctly
                elif re.match(r"Breakpoint 1.*hello\.c.*4", l):
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
        import os, stat, oe.cachedpath
        features = 'IMAGE_INSTALL_append = " selftest-chown"\n'
        self.write_config(features)
        bitbake("core-image-minimal")

        sysconfdir = get_bb_var('sysconfdir', 'selftest-chown')
        def check_ownership(qemu, gid, uid, path):
            self.logger.info("Check ownership of %s", path)
            status, output = qemu.run_serial(r'/bin/stat -c "%U %G" ' + path, timeout=60)
            output = output.split(" ")
            if output[0] != uid or output[1] != gid :
                self.logger.error("Incrrect ownership %s [%s:%s]", path, output[0], output[1])
                return False
            return True

        with runqemu('core-image-minimal') as qemu:
            for path in [ sysconfdir + "/selftest-chown/file",
                          sysconfdir + "/selftest-chown/dir",
                          sysconfdir + "/selftest-chown/symlink",
                          sysconfdir + "/selftest-chown/fifotest/fifo"]:
                if not check_ownership(qemu, "test", "test", path):
                    self.fail('Test ownership %s failed' % path)
