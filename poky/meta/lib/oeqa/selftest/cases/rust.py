# SPDX-License-Identifier: MIT
import subprocess
import time
from oeqa.core.decorator import OETestTag
from oeqa.core.decorator.data import skipIfArch
from oeqa.core.case import OEPTestResultTestCase
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, runqemu
from oeqa.utils.sshcontrol import SSHControl

def parse_results(filename):
    tests = {}
    with open(filename, "r") as f:
        lines = f.readlines()
        for line in lines:
            if "..." in line and "test [" in line:
                test = line.split("test ")[1].split(" ... ")[0]
                if "] " in test:
                    test = test.split("] ", 1)[1]
                result = line.split(" ... ")[1].strip()
                if result == "ok":
                    result = "PASS"
                elif result == "failed":
                    result = "FAIL"
                elif "ignored" in result:
                    result = "SKIPPED"
                if test in tests:
                    if tests[test] != result:
                        print("Duplicate and mismatching result %s for %s" % (result, test))
                    else:
                        print("Duplicate result %s for %s" % (result, test))
                else:
                    tests[test] = result
    return tests

# Total time taken for testing is of about 2hr 20min, with PARALLEL_MAKE set to 40 number of jobs.
@OETestTag("toolchain-system")
@OETestTag("toolchain-user")
@OETestTag("runqemu")
class RustSelfTestSystemEmulated(OESelftestTestCase, OEPTestResultTestCase):

    @skipIfArch(['mips', 'mips64'])
    def test_rust(self, *args, **kwargs):
        # build remote-test-server before image build
        recipe = "rust"
        start_time = time.time()
        bitbake("{} -c test_compile".format(recipe))
        builddir = get_bb_var("RUSTSRC", "rust")
        # build core-image-minimal with required packages
        default_installed_packages = ["libgcc", "libstdc++", "libatomic", "libgomp"]
        features = []
        features.append('IMAGE_FEATURES += "ssh-server-dropbear"')
        features.append('CORE_IMAGE_EXTRA_INSTALL += "{0}"'.format(" ".join(default_installed_packages)))
        self.write_config("\n".join(features))
        bitbake("core-image-minimal")

        # Exclude the test folders that error out while building
        # TODO: Fix the errors and include them for testing
        # no-fail-fast: Run all tests regardless of failure.
        # bless: First runs rustfmt to format the codebase,
        # then runs tidy checks.
        exclude_list =  [
                            'src/bootstrap',
                            'src/doc/rustc',
                            'src/doc/rustdoc',
                            'src/doc/unstable-book',
                            'src/librustdoc',
                            'src/rustdoc-json-types',
                            'src/tools/jsondoclint',
                            'src/tools/lint-docs',
                            'src/tools/replace-version-placeholder',
                            'src/tools/rust-analyzer',
                            'src/tools/rustdoc-themes',
                            'src/tools/rust-installer',
                            'src/tools/suggest-tests',
                            'tests/assembly/asm/aarch64-outline-atomics.rs',
                            'tests/codegen/issues/issue-122805.rs',
                            'tests/codegen/thread-local.rs',
                            'tests/mir-opt/',
                            'tests/run-make',
                            'tests/run-make-fulldeps',
                            'tests/rustdoc',
                            'tests/rustdoc-json',
                            'tests/rustdoc-js-std',
                            'tests/ui/abi/stack-probes-lto.rs',
                            'tests/ui/abi/stack-probes.rs',
                            'tests/ui/codegen/mismatched-data-layouts.rs',
                            'tests/ui/debuginfo/debuginfo-emit-llvm-ir-and-split-debuginfo.rs',
                            'tests/ui-fulldeps/',
                            'tests/ui/process/nofile-limit.rs',
                            'tidyselftest'
                        ]

        exclude_fail_tests = " ".join([" --exclude " + item for item in exclude_list])
        # Add exclude_fail_tests with other test arguments
        testargs =  exclude_fail_tests + " --no-fail-fast --bless"

        # wrap the execution with a qemu instance.
        # Tests are run with 512 tasks in parallel to execute all tests very quickly
        with runqemu("core-image-minimal", runqemuparams = "nographic", qemuparams = "-m 512") as qemu:
            # Copy remote-test-server to image through scp
            host_sys = get_bb_var("RUST_BUILD_SYS", "rust")
            ssh = SSHControl(ip=qemu.ip, logfile=qemu.sshlog, user="root")
            ssh.copy_to(builddir + "/build/" + host_sys + "/stage1-tools-bin/remote-test-server","~/")
            # Execute remote-test-server on image through background ssh
            command = '~/remote-test-server --bind 0.0.0.0:12345 -v'
            sshrun=subprocess.Popen(("ssh", '-o',  'UserKnownHostsFile=/dev/null', '-o',  'StrictHostKeyChecking=no', '-f', "root@%s" % qemu.ip, command), shell=False, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            # Get the values of variables.
            tcpath = get_bb_var("TARGET_SYS", "rust")
            targetsys = get_bb_var("RUST_TARGET_SYS", "rust")
            rustlibpath = get_bb_var("WORKDIR", "rust")
            tmpdir = get_bb_var("TMPDIR", "rust")

            # Set path for target-poky-linux-gcc, RUST_TARGET_PATH and hosttools.
            cmd = "export TARGET_VENDOR=\"-poky\";"
            cmd = cmd + " export PATH=%s/recipe-sysroot-native/usr/bin/python3-native:%s/recipe-sysroot-native/usr/bin:%s/recipe-sysroot-native/usr/bin/%s:%s/hosttools:$PATH;" % (rustlibpath, rustlibpath, rustlibpath, tcpath, tmpdir)
            cmd = cmd + " export RUST_TARGET_PATH=%s/rust-targets;" % rustlibpath
            # Trigger testing.
            cmd = cmd + " export TEST_DEVICE_ADDR=\"%s:12345\";" % qemu.ip
            cmd = cmd + " cd %s; python3 src/bootstrap/bootstrap.py test %s --target %s" % (builddir, testargs, targetsys)
            retval = runCmd(cmd)
            end_time = time.time()

            resultlog = rustlibpath + "/results-log.txt"
            with open(resultlog, "w") as f:
                f.write(retval.output)

            ptestsuite = "rust"
            self.ptest_section(ptestsuite, duration = int(end_time - start_time), logfile=resultlog)
            test_results = parse_results(resultlog)
            for test in test_results:
                self.ptest_result(ptestsuite, test, test_results[test])
