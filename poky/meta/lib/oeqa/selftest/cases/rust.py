# SPDX-License-Identifier: MIT
import os
import subprocess
import time
from oeqa.core.decorator import OETestTag
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
                            'tests/codegen/sse42-implies-crc32.rs',
                            'tests/codegen/thread-local.rs',
                            'tests/codegen/uninit-consts.rs',
                            'tests/pretty/raw-str-nonexpr.rs',
                            'tests/run-make',
                            'tests/run-make/cdylib-fewer-symbols/foo.rs',
                            'tests/run-make/doctests-keep-binaries/t.rs',
                            'tests/run-make-fulldeps',
                            'tests/run-make/issue-22131/foo.rs',
                            'tests/run-make/issue-36710/Makefile',
                            'tests/run-make/issue-47551',
                            'tests/run-make/pgo-branch-weights',
                            'tests/run-make/pgo-gen',
                            'tests/run-make/pgo-gen-lto',
                            'tests/run-make/pgo-indirect-call-promotion',
                            'tests/run-make/pgo-use',
                            'tests/run-make/pointer-auth-link-with-c/Makefile',
                            'tests/run-make/profile',
                            'tests/run-make/static-pie',
                            'tests/run-make/sysroot-crates-are-unstable',
                            'tests/run-make/target-specs',
                            'tests/rustdoc',
                            'tests/rustdoc/async-move-doctest.rs',
                            'tests/rustdoc/async-trait.rs',
                            'tests/rustdoc/auto-traits.rs',
                            'tests/rustdoc/check-source-code-urls-to-def.rs',
                            'tests/rustdoc/comment-in-doctest.rs',
                            'tests/rustdoc/const-generics/const-generics-docs.rs',
                            'tests/rustdoc/cross-crate-hidden-assoc-trait-items.rs',
                            'tests/rustdoc/cross-crate-hidden-impl-parameter.rs',
                            'tests/rustdoc/cross-crate-links.rs',
                            'tests/rustdoc/cross-crate-primitive-doc.rs',
                            'tests/rustdoc/doctest-manual-crate-name.rs',
                            'tests/rustdoc/edition-doctest.rs',
                            'tests/rustdoc/edition-flag.rs',
                            'tests/rustdoc/elided-lifetime.rs',
                            'tests/rustdoc/external-macro-src.rs',
                            'tests/rustdoc/extern-html-root-url.rs',
                            'tests/rustdoc/extern-impl-trait.rs',
                            'tests/rustdoc/hide-unstable-trait.rs',
                            'tests/rustdoc/inline_cross/add-docs.rs',
                            'tests/rustdoc/inline_cross/default-trait-method.rs',
                            'tests/rustdoc/inline_cross/dyn_trait.rs',
                            'tests/rustdoc/inline_cross/impl_trait.rs',
                            'tests/rustdoc/inline_cross/issue-24183.rs',
                            'tests/rustdoc/inline_cross/macros.rs',
                            'tests/rustdoc/inline_cross/trait-vis.rs',
                            'tests/rustdoc/inline_cross/use_crate.rs',
                            'tests/rustdoc/intra-doc-crate/self.rs',
                            'tests/rustdoc/intra-doc/cross-crate/additional_doc.rs',
                            'tests/rustdoc/intra-doc/cross-crate/basic.rs',
                            'tests/rustdoc/intra-doc/cross-crate/crate.rs',
                            'tests/rustdoc/intra-doc/cross-crate/hidden.rs',
                            'tests/rustdoc/intra-doc/cross-crate/macro.rs',
                            'tests/rustdoc/intra-doc/cross-crate/module.rs',
                            'tests/rustdoc/intra-doc/cross-crate/submodule-inner.rs',
                            'tests/rustdoc/intra-doc/cross-crate/submodule-outer.rs',
                            'tests/rustdoc/intra-doc/cross-crate/traits.rs',
                            'tests/rustdoc/intra-doc/extern-builtin-type-impl.rs',
                            'tests/rustdoc/intra-doc/extern-crate-only-used-in-link.rs',
                            'tests/rustdoc/intra-doc/extern-crate.rs',
                            'tests/rustdoc/intra-doc/extern-inherent-impl.rs',
                            'tests/rustdoc/intra-doc/extern-reference-link.rs',
                            'tests/rustdoc/intra-doc/issue-103463.rs',
                            'tests/rustdoc/intra-doc/issue-104145.rs',
                            'tests/rustdoc/intra-doc/issue-66159.rs',
                            'tests/rustdoc/intra-doc/pub-use.rs',
                            'tests/rustdoc/intra-doc/reexport-additional-docs.rs',
                            'tests/rustdoc/issue-18199.rs',
                            'tests/rustdoc/issue-23106.rs',
                            'tests/rustdoc/issue-23744.rs',
                            'tests/rustdoc/issue-25944.rs',
                            'tests/rustdoc/issue-30252.rs',
                            'tests/rustdoc/issue-38129.rs',
                            'tests/rustdoc/issue-40936.rs',
                            'tests/rustdoc/issue-43153.rs',
                            'tests/rustdoc/issue-46727.rs',
                            'tests/rustdoc/issue-48377.rs',
                            'tests/rustdoc/issue-48414.rs',
                            'tests/rustdoc/issue-53689.rs',
                            'tests/rustdoc/issue-54478-demo-allocator.rs',
                            'tests/rustdoc/issue-57180.rs',
                            'tests/rustdoc/issue-61592.rs',
                            'tests/rustdoc/issue-73061-cross-crate-opaque-assoc-type.rs',
                            'tests/rustdoc/issue-75588.rs',
                            'tests/rustdoc/issue-85454.rs',
                            'tests/rustdoc/issue-86620.rs',
                            'tests/rustdoc-json',
                            'tests/rustdoc-js-std',
                            'tests/rustdoc/macro_pub_in_module.rs',
                            'tests/rustdoc/masked.rs',
                            'tests/rustdoc/normalize-assoc-item.rs',
                            'tests/rustdoc/no-stack-overflow-25295.rs',
                            'tests/rustdoc/primitive-reexport.rs',
                            'tests/rustdoc/process-termination.rs',
                            'tests/rustdoc/pub-extern-crate.rs',
                            'tests/rustdoc/pub-use-extern-macros.rs',
                            'tests/rustdoc/reexport-check.rs',
                            'tests/rustdoc/reexport-dep-foreign-fn.rs',
                            'tests/rustdoc/reexport-doc.rs',
                            'tests/rustdoc/reexports-priv.rs',
                            'tests/rustdoc/reexports.rs',
                            'tests/rustdoc/rustc,-incoherent-impls.rs',
                            'tests/rustdoc/test_option_check/bar.rs',
                            'tests/rustdoc/test_option_check/test.rs',
                            'tests/rustdoc/trait-alias-mention.rs',
                            'tests/rustdoc/trait-visibility.rs',
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
                            'tests/ui-fulldeps/deriving-encodable-decodable-box.rs',
                            'tests/ui-fulldeps/deriving-encodable-decodable-cell-refcell.rs',
                            'tests/ui-fulldeps/deriving-global.rs',
                            'tests/ui-fulldeps/deriving-hygiene.rs',
                            'tests/ui-fulldeps/dropck_tarena_sound_drop.rs',
                            'tests/ui-fulldeps/empty-struct-braces-derive.rs',
                            'tests/ui-fulldeps/internal-lints/bad_opt_access.rs',
                            'tests/ui-fulldeps/internal-lints/bad_opt_access.stderr',
                            'tests/ui-fulldeps/internal-lints/default_hash_types.rs',
                            'tests/ui-fulldeps/internal-lints/diagnostics.rs',
                            'tests/ui-fulldeps/internal-lints/lint_pass_impl_without_macro.rs',
                            'tests/ui-fulldeps/internal-lints/qualified_ty_ty_ctxt.rs',
                            'tests/ui-fulldeps/internal-lints/query_stability.rs',
                            'tests/ui-fulldeps/internal-lints/rustc_pass_by_value.rs',
                            'tests/ui-fulldeps/internal-lints/ty_tykind_usage.rs',
                            'tests/ui-fulldeps/issue-14021.rs',
                            'tests/ui-fulldeps/lint-group-denied-lint-allowed.rs',
                            'tests/ui-fulldeps/lint-group-forbid-always-trumps-cli.rs',
                            'tests/ui-fulldeps/lint-pass-macros.rs',
                            'tests/ui-fulldeps/regions-mock-tcx.rs',
                            'tests/ui-fulldeps/rustc_encodable_hygiene.rs',
                            'tests/ui-fulldeps/session-diagnostic/enforce_slug_naming.rs',
                            'tests/ui/functions-closures/fn-help-with-err.rs',
                            'tests/ui/linkage-attr/issue-10755.rs',
                            'tests/ui/macros/restricted-shadowing-legacy.rs',
                            'tests/ui/process/nofile-limit.rs',
                            'tests/ui/process/process-panic-after-fork.rs',
                            'tests/ui/process/process-sigpipe.rs',
                            'tests/ui/simd/target-feature-mixup.rs',
                            'tests/ui/structs-enums/multiple-reprs.rs'
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
            cmd = " export PATH=%s/recipe-sysroot-native/usr/bin:$PATH;" % rustlibpath
            cmd = cmd + " export TARGET_VENDOR=\"-poky\";"
            cmd = cmd + " export PATH=%s/recipe-sysroot-native/usr/bin/%s:%s/hosttools:$PATH;" % (rustlibpath, tcpath, tmpdir)
            cmd = cmd + " export RUST_TARGET_PATH=%s/rust-targets;" % rustlibpath
            # Trigger testing.
            cmd = cmd + " export TEST_DEVICE_ADDR=\"%s:12345\";" % qemu.ip
            cmd = cmd + " cd %s; python3 src/bootstrap/bootstrap.py test %s --target %s > summary.txt 2>&1;" % (builddir, testargs, targetsys)
            runCmd(cmd)
            end_time = time.time()

            ptestsuite = "rust"
            self.ptest_section(ptestsuite, duration = int(end_time - start_time), logfile = builddir + "/summary.txt")
            filename = builddir + "/summary.txt"
            test_results = parse_results(filename)
            for test in test_results:
                self.ptest_result(ptestsuite, test, test_results[test])
