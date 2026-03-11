#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
import os
import time
from oeqa.core.decorator import OETestTag
from oeqa.core.case import OEPTestResultTestCase
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_var, get_bb_vars, runqemu

def parse_values(content):
    for i in content:
        for v in ["PASS", "FAIL", "XPASS", "XFAIL", "UNRESOLVED", "UNSUPPORTED", "UNTESTED", "ERROR", "WARNING"]:
            if i.startswith(v + ": "):
                yield i[len(v) + 2:].strip(), v
                break

class GccSelfTestBase(OESelftestTestCase, OEPTestResultTestCase):
    def check_skip(self, suite):
        targets = get_bb_var("RUNTIMETARGET", "gcc-runtime").split()
        if suite not in targets:
            self.skipTest("Target does not use {0}".format(suite))

    def run_check(self, *suites, ssh = None):
        targets = set()
        for s in suites:
            if s == "gcc":
                targets.add("check-gcc-c")
            elif s == "g++":
                targets.add("check-gcc-c++")
            else:
                targets.add("check-target-{}".format(s))

        # configure ssh target
        features = []
        features.append('MAKE_CHECK_TARGETS = "{0}"'.format(" ".join(targets)))
        if ssh is not None:
            features.append('TOOLCHAIN_TEST_TARGET = "linux-ssh"')
            features.append('TOOLCHAIN_TEST_HOST = "{0}"'.format(ssh))
            features.append('TOOLCHAIN_TEST_HOST_USER = "root"')
            features.append('TOOLCHAIN_TEST_HOST_PORT = "22"')
        self.write_config("\n".join(features))

        recipe = "gcc-runtime"

        start_time = time.time()

        bitbake("{} -c check".format(recipe))

        end_time = time.time()

        bb_vars = get_bb_vars(["B", "TARGET_SYS"], recipe)
        builddir, target_sys = bb_vars["B"], bb_vars["TARGET_SYS"]

        for suite in suites:
            sumspath = os.path.join(builddir, "gcc", "testsuite", suite, "{0}.sum".format(suite))
            if not os.path.exists(sumspath): # check in target dirs
                sumspath = os.path.join(builddir, target_sys, suite, "testsuite", "{0}.sum".format(suite))
            if not os.path.exists(sumspath): # handle libstdc++-v3 -> libstdc++
                sumspath = os.path.join(builddir, target_sys, suite, "testsuite", "{0}.sum".format(suite.split("-")[0]))
            logpath = os.path.splitext(sumspath)[0] + ".log"

            ptestsuite = "gcc-{}".format(suite) if suite != "gcc" else suite
            ptestsuite = ptestsuite + "-user" if ssh is None else ptestsuite
            self.ptest_section(ptestsuite, duration = int(end_time - start_time), logfile = logpath)
            with open(sumspath, "r") as f:
                for test, result in parse_values(f):
                    self.ptest_result(ptestsuite, test, result)

    def run_check_emulated(self, *args, **kwargs):
        # build core-image-minimal with required packages
        default_installed_packages = ["libgcc", "libstdc++", "libatomic", "libgomp"]
        features = []
        features.append('IMAGE_FEATURES += "ssh-server-openssh"')
        features.append('CORE_IMAGE_EXTRA_INSTALL += "{0}"'.format(" ".join(default_installed_packages)))
        self.write_config("\n".join(features))
        bitbake("core-image-minimal")

        # wrap the execution with a qemu instance
        with runqemu("core-image-minimal", runqemuparams = "nographic") as qemu:
            # validate that SSH is working
            status, _ = qemu.run("uname")
            self.assertEqual(status, 0)
            qemu.run('echo "MaxStartups 75:30:100" >> /etc/ssh/sshd_config')
            qemu.run('service sshd restart')

            return self.run_check(*args, ssh=qemu.ip, **kwargs)

@OETestTag("toolchain-user")
class GccCrossSelfTest(GccSelfTestBase):
    def test_cross_gcc(self):
        self.run_check("gcc")

@OETestTag("toolchain-user")
class GxxCrossSelfTest(GccSelfTestBase):
    def test_cross_gxx(self):
        self.run_check("g++")

@OETestTag("toolchain-user")
class GccLibAtomicSelfTest(GccSelfTestBase):
    def test_libatomic(self):
        self.run_check("libatomic")

@OETestTag("toolchain-user")
class GccLibGompSelfTest(GccSelfTestBase):
    def test_libgomp(self):
        self.run_check("libgomp")

@OETestTag("toolchain-user")
class GccLibStdCxxSelfTest(GccSelfTestBase):
    def test_libstdcxx(self):
        self.run_check("libstdc++-v3")

@OETestTag("toolchain-user")
class GccLibSspSelfTest(GccSelfTestBase):
    def test_libssp(self):
        self.check_skip("libssp")
        self.run_check("libssp")

@OETestTag("toolchain-user")
class GccLibItmSelfTest(GccSelfTestBase):
    def test_libitm(self):
        self.check_skip("libitm")
        self.run_check("libitm")

@OETestTag("toolchain-system")
@OETestTag("runqemu")
class GccCrossSelfTestSystemEmulated(GccSelfTestBase):
    def test_cross_gcc(self):
        self.run_check_emulated("gcc")

@OETestTag("toolchain-system")
@OETestTag("runqemu")
class GxxCrossSelfTestSystemEmulated(GccSelfTestBase):
    def test_cross_gxx(self):
        self.run_check_emulated("g++")

@OETestTag("toolchain-system")
@OETestTag("runqemu")
class GccLibAtomicSelfTestSystemEmulated(GccSelfTestBase):
    def test_libatomic(self):
        self.run_check_emulated("libatomic")

@OETestTag("toolchain-system")
@OETestTag("runqemu")
class GccLibGompSelfTestSystemEmulated(GccSelfTestBase):
    def test_libgomp(self):
        self.run_check_emulated("libgomp")

@OETestTag("toolchain-system")
@OETestTag("runqemu")
class GccLibStdCxxSelfTestSystemEmulated(GccSelfTestBase):
    def test_libstdcxx(self):
        self.run_check_emulated("libstdc++-v3")

@OETestTag("toolchain-system")
@OETestTag("runqemu")
class GccLibSspSelfTestSystemEmulated(GccSelfTestBase):
    def test_libssp(self):
        self.check_skip("libssp")
        self.run_check_emulated("libssp")

@OETestTag("toolchain-system")
@OETestTag("runqemu")
class GccLibItmSelfTestSystemEmulated(GccSelfTestBase):
    def test_libitm(self):
        self.check_skip("libitm")
        self.run_check_emulated("libitm")

