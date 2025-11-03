# SPDX-License-Identifier: MIT
import os
import subprocess
import time
from oeqa.core.decorator import OETestTag
from oeqa.core.decorator.data import skipIfArch
from oeqa.core.case import OEPTestResultTestCase
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_bb_vars, runqemu, Command
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
        # Disable Rust Oe-selftest
        #self.skipTest("The Rust Oe-selftest is disabled.")

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
                            'compiler/rustc',
                            'compiler/rustc_interface/src/tests.rs',
                            'library/panic_abort',
                            'library/panic_unwind',
                            'library/test/src/stats/tests.rs',
                            'src/bootstrap/builder/tests.rs',
                            'src/doc/rustc',
                            'src/doc/rustdoc',
                            'src/doc/unstable-book',
                            'src/librustdoc',
                            'src/rustdoc-json-types',
                            'src/tools/compiletest/src/common.rs',
                            'src/tools/lint-docs',
                            'src/tools/rust-analyzer',
                            'src/tools/rustdoc-themes',
                            'src/tools/tidy',
                            'tests/assembly/asm/aarch64-outline-atomics.rs',
                            'tests/codegen/abi-main-signature-32bit-c-int.rs',
                            'tests/codegen/abi-repr-ext.rs',
                            'tests/codegen/abi-x86-interrupt.rs',
                            'tests/codegen/branch-protection.rs',
                            'tests/codegen/catch-unwind.rs',
                            'tests/codegen/cf-protection.rs',
                            'tests/codegen/enum-bounds-check-derived-idx.rs',
                            'tests/codegen/force-unwind-tables.rs',
                            'tests/codegen/intrinsic-no-unnamed-attr.rs',
                            'tests/codegen/issues/issue-103840.rs',
                            'tests/codegen/issues/issue-47278.rs',
                            'tests/codegen/issues/issue-73827-bounds-check-index-in-subexpr.rs',
                            'tests/codegen/lifetime_start_end.rs',
                            'tests/codegen/local-generics-in-exe-internalized.rs',
                            'tests/codegen/match-unoptimized.rs',
                            'tests/codegen/noalias-rwlockreadguard.rs',
                            'tests/codegen/non-terminate/nonempty-infinite-loop.rs',
                            'tests/codegen/noreturn-uninhabited.rs',
                            'tests/codegen/repr-transparent-aggregates-3.rs',
                            'tests/codegen/riscv-abi/call-llvm-intrinsics.rs',
                            'tests/codegen/riscv-abi/riscv64-lp64f-lp64d-abi.rs',
                            'tests/codegen/riscv-abi/riscv64-lp64d-abi.rs',
                            'tests/codegen/sse42-implies-crc32.rs',
                            'tests/codegen/thread-local.rs',
                            'tests/codegen/uninit-consts.rs',
                            'tests/pretty/raw-str-nonexpr.rs',
                            'tests/run-make',
                            'tests/run-make-fulldeps',
                            'tests/rustdoc',
                            'tests/rustdoc-json',
                            'tests/rustdoc-js-std',
                            'tests/rustdoc-ui/cfg-test.rs',
                            'tests/rustdoc-ui/check-cfg-test.rs',
                            'tests/rustdoc-ui/display-output.rs',
                            'tests/rustdoc-ui/doc-comment-multi-line-attr.rs',
                            'tests/rustdoc-ui/doc-comment-multi-line-cfg-attr.rs',
                            'tests/rustdoc-ui/doc-test-doctest-feature.rs',
                            'tests/rustdoc-ui/doctest-multiline-crate-attribute.rs',
                            'tests/rustdoc-ui/doctest-output.rs',
                            'tests/rustdoc-ui/doc-test-rustdoc-feature.rs',
                            'tests/rustdoc-ui/failed-doctest-compile-fail.rs',
                            'tests/rustdoc-ui/issue-80992.rs',
                            'tests/rustdoc-ui/issue-91134.rs',
                            'tests/rustdoc-ui/nocapture-fail.rs',
                            'tests/rustdoc-ui/nocapture.rs',
                            'tests/rustdoc-ui/no-run-flag.rs',
                            'tests/rustdoc-ui/run-directory.rs',
                            'tests/rustdoc-ui/test-no_std.rs',
                            'tests/rustdoc-ui/test-type.rs',
                            'tests/rustdoc/unit-return.rs',
                            'tests/ui/abi/stack-probes-lto.rs',
                            'tests/ui/abi/stack-probes.rs',
                            'tests/ui/array-slice-vec/subslice-patterns-const-eval-match.rs',
                            'tests/ui/asm/x86_64/sym.rs',
                            'tests/ui/associated-type-bounds/fn-apit.rs',
                            'tests/ui/associated-type-bounds/fn-dyn-apit.rs',
                            'tests/ui/associated-type-bounds/fn-wrap-apit.rs',
                            'tests/ui/debuginfo/debuginfo-emit-llvm-ir-and-split-debuginfo.rs',
                            'tests/ui/drop/dynamic-drop.rs',
                            'tests/ui/empty_global_asm.rs',
                            'tests/ui/functions-closures/fn-help-with-err.rs',
                            'tests/ui/linkage-attr/issue-10755.rs',
                            'tests/ui/macros/restricted-shadowing-legacy.rs',
                            'tests/ui/process/nofile-limit.rs',
                            'tests/ui/process/process-panic-after-fork.rs',
                            'tests/ui/process/process-sigpipe.rs',
                            'tests/ui/simd/target-feature-mixup.rs',
                            'tests/ui/structs-enums/multiple-reprs.rs',
                            'src/tools/jsondoclint',
                            'src/tools/replace-version-placeholder',
                            'tests/codegen/abi-efiapi.rs',
                            'tests/codegen/abi-sysv64.rs',
                            'tests/codegen/align-byval.rs',
                            'tests/codegen/align-fn.rs',
                            'tests/codegen/asm-powerpc-clobbers.rs',
                            'tests/codegen/async-fn-debug-awaitee-field.rs',
                            'tests/codegen/binary-search-index-no-bound-check.rs',
                            'tests/codegen/call-metadata.rs',
                            'tests/codegen/debug-column.rs',
                            'tests/codegen/debug-limited.rs',
                            'tests/codegen/debuginfo-generic-closure-env-names.rs',
                            'tests/codegen/drop.rs',
                            'tests/codegen/dst-vtable-align-nonzero.rs',
                            'tests/codegen/enable-lto-unit-splitting.rs',
                            'tests/codegen/enum/enum-u128.rs',
                            'tests/codegen/fn-impl-trait-self.rs',
                            'tests/codegen/inherit_overflow.rs',
                            'tests/codegen/inline-function-args-debug-info.rs',
                            'tests/codegen/intrinsics/mask.rs',
                            'tests/codegen/intrinsics/transmute-niched.rs',
                            'tests/codegen/issues/issue-73258.rs',
                            'tests/codegen/issues/issue-75546.rs',
                            'tests/codegen/issues/issue-77812.rs',
                            'tests/codegen/issues/issue-98156-const-arg-temp-lifetime.rs',
                            'tests/codegen/llvm-ident.rs',
                            'tests/codegen/mainsubprogram.rs',
                            'tests/codegen/move-operands.rs',
                            'tests/codegen/repr/transparent-mips64.rs',
                            'tests/mir-opt/',
                            'tests/rustdoc-json',
                            'tests/rustdoc-ui/doc-test-rustdoc-feature.rs',
                            'tests/rustdoc-ui/no-run-flag.rs',
                            'tests/ui-fulldeps/',
                            'tests/ui/numbers-arithmetic/u128.rs'
                        ]

        exclude_fail_tests = " ".join([" --exclude " + item for item in exclude_list])
        # Add exclude_fail_tests with other test arguments
        testargs =  exclude_fail_tests + " --doc --no-fail-fast --bless"

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
