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
            qemu = s.enter_context(runqemu("core-image-minimal", runqemuparams = "nographic", qemuparams = "-m 1024"))

            # validate that SSH is working
            status, _ = qemu.run("uname")
            self.assertEqual(status, 0)

            # setup nfs mount
            if qemu.run("mkdir -p \"{0}\"".format(tmpdir))[0] != 0:
                raise Exception("Failed to setup NFS mount directory on target")
            mountcmd = "mount -o noac,nfsvers=3,port={0},mountport={1} \"{2}:{3}\" \"{3}\"".format(nfsport, mountport, qemu.server_ip, tmpdir)
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

