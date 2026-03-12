#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
import os
import time
import contextlib
from oeqa.core.decorator import OETestTag
from oeqa.core.case import OEPTestResultTestCase
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_var, runqemu
from oeqa.utils.nfs import unfs_server

def parse_values(content):
    for i in content:
        for v in ["PASS", "FAIL", "XPASS", "XFAIL", "UNRESOLVED", "UNSUPPORTED", "UNTESTED", "ERROR", "WARNING"]:
            if i.startswith(v + ": "):
                yield i[len(v) + 2:].strip(), v
                break

class GlibcSelfTestBase(OESelftestTestCase, OEPTestResultTestCase):
    """
    Base class for running the glibc test suite (bitbake glibc-testsuite -c check).

    The glibc-testsuite recipe cross-compiles the glibc tests, which are then
    executed via Autotools' make check. The check-test-wrapper script dispatches
    each individual test to either QEMU user-space emulation or a remote SSH
    target, depending on TOOLCHAIN_TEST_TARGET. For more details see the
    do_check task of the glibc-testsuite recipe.

    Unlike ptests, the glibc tests are not installed via do_install but instead
    run in-place directly from the build directory (B). This is true for both
    execution modes implemented by this base class via run_check():

    User-space mode (ssh=None):
        TOOLCHAIN_TEST_TARGET is left at its default ("user"), so
        check-test-wrapper runs each test binary under QEMU user-space
        emulation against the target sysroot. Results are collected under
        the ptest suite name "glibc-user".

    SSH/system mode (ssh=<ip>):
        TOOLCHAIN_TEST_TARGET is set to "ssh" and the SSH connection details
        (host, user, port) are configured so check-test-wrapper forwards each
        test to the remote target over SSH. Since the tests run from the build
        directory, that directory must be accessible on the target, which is
        achieved by NFS-mounting it into the QEMU system running core-image-minimal.
        Results are collected under the ptest suite name "glibc".

    In both modes, results are parsed from the tests.sum file produced by
    make check and recorded via the OEPTestResultTestCase ptest helpers.
    """
    def run_check(self, ssh = None):
        # configure ssh target
        features = []
        if ssh is not None:
            features.append('TOOLCHAIN_TEST_TARGET = "ssh"')
            features.append('TOOLCHAIN_TEST_HOST = "{0}"'.format(ssh))
            features.append('TOOLCHAIN_TEST_HOST_USER = "root"')
            features.append('TOOLCHAIN_TEST_HOST_PORT = "22"')
            # force single threaded test execution
            features.append('EGLIBCPARALLELISM:task-check:pn-glibc-testsuite = "PARALLELMFLAGS="-j1""')
        self.write_config("\n".join(features))

        start_time = time.time()

        bitbake("glibc-testsuite -c check")

        end_time = time.time()

        builddir = get_bb_var("B", "glibc-testsuite")

        ptestsuite = "glibc-user" if ssh is None else "glibc"
        self.ptest_section(ptestsuite, duration = int(end_time - start_time))
        with open(os.path.join(builddir, "tests.sum"), "r",  errors='replace') as f:
            for test, result in parse_values(f):
                self.ptest_result(ptestsuite, test, result)

    def run_check_emulated(self):
        with contextlib.ExitStack() as s:
            # use the base work dir, as the nfs mount, since the recipe directory may not exist
            tmpdir = get_bb_var("BASE_WORKDIR")
            nfsport, mountport = s.enter_context(unfs_server(tmpdir, udp = False))

            # build core-image-minimal with required packages
            default_installed_packages = [
                "glibc-charmaps",
                "libgcc",
                "libstdc++",
                "libatomic",
                "libgomp",
                # "python3",
                # "python3-pexpect",
                "nfs-utils",
                ]
            features = []
            features.append('IMAGE_FEATURES += "ssh-server-openssh"')
            features.append('CORE_IMAGE_EXTRA_INSTALL += "{0}"'.format(" ".join(default_installed_packages)))
            self.write_config("\n".join(features))
            bitbake("core-image-minimal")

            # start runqemu
            qemu = s.enter_context(runqemu("core-image-minimal", runqemuparams = "nographic", qemuparams = "-m 2048"))

            # validate that SSH is working
            status, _ = qemu.run("uname")
            self.assertEqual(status, 0)

            # setup nfs mount
            if qemu.run("mkdir -p \"{0}\"".format(tmpdir))[0] != 0:
                raise Exception("Failed to setup NFS mount directory on target")
            mountcmd = "mount -o noac,nfsvers=3,local_lock=all,port={0},mountport={1} \"{2}:{3}\" \"{3}\"".format(nfsport, mountport, qemu.server_ip, tmpdir)
            status, output = qemu.run(mountcmd)
            if status != 0:
                raise Exception("Failed to setup NFS mount on target ({})".format(repr(output)))

            self.run_check(ssh = qemu.ip)

@OETestTag("toolchain-user")
class GlibcSelfTest(GlibcSelfTestBase):
    def test_glibc(self):
        self.run_check()

@OETestTag("toolchain-system")
@OETestTag("runqemu")
class GlibcSelfTestSystemEmulated(GlibcSelfTestBase):
    def test_glibc(self):
        self.run_check_emulated()

