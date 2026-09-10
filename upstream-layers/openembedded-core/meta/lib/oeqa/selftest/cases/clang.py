#
# Copyright (c) 2026 by Wind River Systems, Inc.
#
# SPDX-License-Identifier: MIT
#
import contextlib
from oeqa.core.decorator import OETestTag
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_var, runqemu
from oeqa.utils.nfs import unfs_server

class ClangFamilyBase(OESelftestTestCase):

    ALL_ARCHS = [
        "aarch64", "arm", "i386", "x86_64", "x86",
        "mips", "riscv", "riscv64", "ppc", "ppc64",
        "hexagon", "sparc", "sparcv9", "msp430",
        "loongarch"
    ]

    EXCLUDE_OS = [
        "Mac", "macho", "Darwin", "OSX", "wasm",
        "Windows", "Win", "MinGW", "COFF", "zos",
        "FreeBSD", "aix", "fuchsia", "ve"
    ]

    DEFAULT_PACKAGES = [
        "libgcc",
        "libstdc++",
        "llvm",
        "python3",
        "python3-core",
        "python3-modules",
        "nfs-utils",
    ]

    def setUp(self):
        super().setUp()
        self.write_config('CLANG_ENABLE_TESTSUITE = "1"')
        self.target_arch = get_bb_var("TARGET_ARCH")
        self.tmpdir = get_bb_var("TMPDIR")
        arches = [a for a in self.ALL_ARCHS if a != self.target_arch]
        self.filter_out = "|".join(arches + self.EXCLUDE_OS)

    def build_core_image(self):
        features = [
            'CLANG_ENABLE_TESTSUITE = "1"',
            'IMAGE_FEATURES += "ssh-server-openssh"',
            'CORE_IMAGE_EXTRA_INSTALL += "{}"'.format(" ".join(self.DEFAULT_PACKAGES))
        ]
        self.write_config("\n".join(features))
        bitbake("core-image-minimal")

    def start_qemu_nfs(self):
        ctx = contextlib.ExitStack()
        s = ctx.__enter__()
        nfsport, mountport = s.enter_context(unfs_server(self.tmpdir, udp=False))

        # 32-bit ARM can only address up to ~3GB
        if self.target_arch == "arm":
            qemu_mem = 3072
        else:
            qemu_mem = 8192

        qemu = s.enter_context(
            runqemu("core-image-minimal", runqemuparams="nographic",
                    qemuparams=f"-m {qemu_mem} -smp 4")
        )

        status, _ = qemu.run("uname")
        if status != 0:
            raise AssertionError("QEMU SSH check failed")

        status, _ = qemu.run(f"mkdir -p {self.tmpdir}")
        if status != 0:
            raise AssertionError("Failed to create TMPDIR on target")

        mountcmd = (
            f"mount -o nfsvers=3,local_lock=all,port={nfsport},mountport={mountport} "
            f"\"{qemu.server_ip}:{self.tmpdir}\" \"{self.tmpdir}\""
        )
        status, output = qemu.run(mountcmd)
        if status != 0:
            raise AssertionError(f"NFS mount failed: {output}")
        return ctx, qemu

    def run_llvm_lit(self, recipe, extra_filter=None):
        build_dir = get_bb_var("B", recipe)
        workdir = get_bb_var("WORKDIR", recipe)
        guest_result = f"/tmp/{recipe}-target-results.json"
        host_result = f"{workdir}/{recipe}-target-results.log"

        filter_regex = self.filter_out
        if extra_filter:
            filter_regex += "|" + "|".join(extra_filter)

        cmd = (
             f"cd {build_dir}/bin && "
             f"python3 ./llvm-lit -j 4 --filter-out '{filter_regex}' ../test -o {guest_result}"
        )

        return cmd, guest_result, host_result

@OETestTag("toolchain-system")
@OETestTag("toolchain-user")
@OETestTag("runqemu")
class LLVMSelfTestSystemEmulated(ClangFamilyBase):

    LLVM_EXTRA_EXCLUDE = [
        "ADT/.*",
        "BugPoint/compile-custom\\.ll$",
        "BugPoint/replace-funcs-with-null\\.ll$",
        "CodeGen/AMDGPU/lds-run-twice-absolute-md\\.ll$",
        "CodeGen/AMDGPU/lds-run-twice\\.ll$",
        "CodeGen/Generic/fp128-math-libcalls\\.ll$",
        "CodeGen/Thumb/2009-08-20-ISelBug\\.ll$",
        "CodeGen/Thumb/2010-07-15-debugOrdering\\.ll$",
        "CodeGen/Thumb/2014-06-10-thumb1-ldst-opt-bug\\.ll$",
        "CodeGen/Thumb/PR17309\\.ll$",
        "CodeGen/Thumb/dyn-stackalloc\\.ll$",
        "CodeGen/Thumb/frame-access\\.ll$",
        "CodeGen/Thumb/frame-chain\\.ll$",
        "CodeGen/Thumb/ldm-merge-call\\.ll$",
        "CodeGen/Thumb/ldm-merge-struct\\.ll$",
        "CodeGen/Thumb/pop\\.ll$",
        "CodeGen/Thumb/pr35836\\.ll$",
        "CodeGen/Thumb/pr35836_2\\.ll$",
        "CodeGen/Thumb/scmp\\.ll$",
        "CodeGen/Thumb/smul_fix\\.ll$",
        "CodeGen/Thumb/stack-guard-xo\\.ll$",
        "CodeGen/Thumb/stm-deprecated\\.ll$",
        "CodeGen/Thumb/stm-merge\\.ll$",
        "CodeGen/Thumb/thumb-ldm\\.ll$",
        "CodeGen/Thumb/ucmp\\.ll$",
        "CodeGen/Thumb/vargs\\.ll$",
        "CodeGen/Thumb2/2009-08-21-PostRAKill4\\.ll$",
        "CodeGen/Thumb2/2009-09-01-PostRAProlog\\.ll$",
        "CodeGen/Thumb2/constant-islands\\.ll$",
        "CodeGen/Thumb2/ldr-str-imm12\\.ll$",
        "CodeGen/Thumb2/pacbti-m-varargs-1\\.ll$",
        "CodeGen/Thumb2/pacbti-m-varargs-2\\.ll$",
        "CodeGen/Thumb2/thumb2-ldm\\.ll$",
        "ExecutionEngine/JITLink/Generic/llvm-jitlink-option-show-addrs\\.test$",
        "ExecutionEngine/JITLink/Generic/sectcreate\\.test$",
        "ExecutionEngine/JITLink/Generic/waiting-on-graph-capture-replay\\.test$",
        "ExecutionEngine/MCJIT/.*",
        "Other/can-execute\\.txt$",
        "Other/spirv-sim/.*",
        "Transforms/LoopLoadElim/.*",
        "tools/llvm-cas/validation\\.test$",
        "tools/llvm-cgdata/merge-combined-funcmap-hashtree\\.test$",
        "tools/llvm-cgdata/merge-funcmap-concat\\.test$",
        "tools/llvm-cgdata/merge-funcmap-double\\.test$",
        "tools/llvm-cgdata/merge-funcmap-single\\.test$",
        "tools/llvm-cgdata/merge-hashtree-concat\\.test$",
        "tools/llvm-cgdata/merge-hashtree-double\\.test$",
        "tools/llvm-cgdata/merge-hashtree-single\\.test$",
        "tools/llvm-locstats/locstats\\.ll$",
        "tools/llvm-locstats/no_scope_bytes\\.ll$",
        "tools/llvm-objcopy/DXContainer/dump-section\\.yaml$",
        "tools/llvm-objcopy/DXContainer/dump-section-priv\\.yaml$",
        "tools/llvm-objcopy/ELF/basic-binary-copy\\.test$",
        "tools/llvm-objcopy/ELF/binary-first-seg-offset-zero\\.test$",
        "tools/llvm-objcopy/ELF/binary-no-paddr\\.test$",
        "tools/llvm-objcopy/ELF/binary-paddr\\.test$",
        "tools/llvm-objcopy/ELF/binary-segment-layout\\.test$",
        "tools/llvm-objcopy/ELF/check-addr-offset-align-binary\\.test$",
        "tools/llvm-objcopy/ELF/dump-section\\.test$",
        "tools/llvm-objcopy/ELF/gap-fill\\.test$",
        "tools/llvm-objcopy/ELF/pad-to\\.test$",
        "tools/llvm-objcopy/ELF/parent-loop-check\\.test$",
        "tools/llvm-objcopy/ELF/strip-all-gnu\\.test$",
        "tools/llvm-objcopy/ELF/strip-sections-keep\\.test$",
        "tools/llvm-objcopy/ELF/strip-sections-only-section\\.test$",
        "tools/llvm-objcopy/ELF/strip-sections\\.test$",
        "tools/llvm-objcopy/ELF/update-section\\.test$",
        "tools/llvm-original-di-preservation/acceptance-test\\.test$",
        "tools/llvm-original-di-preservation/basic\\.test$",
        "tools/llvm-reduce/temporary-files-as-bitcode-split\\.ll$",
        "tools/not/disable-symbolization\\.test$",
        "tools/opt-viewer/.*",
        "tools/UpdateTestChecks/.*",
        "tools/yaml2obj/ELF/custom-fill\\.yaml$",
        "tools/yaml2obj/ELF/header-elfdatanone\\.yaml$",
        "tools/yaml2obj/ELF/header-sh-fields\\.yaml$",
        "tools/yaml2obj/GOFF/GOFF-header-end\\.yaml$",
        "tools/yaml2obj/GOFF/GOFF-header-settings\\.yaml$",
    ]

    def test_llvm(self):
        bitbake("llvm -c install")
        self.build_core_image()
        ctx, qemu = self.start_qemu_nfs()
        with ctx:
            cmd, guest_result, host_result = self.run_llvm_lit(
                "llvm", extra_filter=self.LLVM_EXTRA_EXCLUDE
            )
            status, output = qemu.run(cmd, timeout=7200)
            if status != 0:
                raise AssertionError(f"llvm-lit failed for LLVM: {output}")
            status, _ = qemu.run(f"cp {guest_result} {host_result}")
            if status != 0:
                raise AssertionError("Failed to copy LLVM lit results back to host")

@OETestTag("toolchain-system")
@OETestTag("toolchain-user")
@OETestTag("runqemu")
class ClangSelfTestSystemEmulated(ClangFamilyBase):

    CLANG_EXTRA_EXCLUDE = [
        "APINotes/yaml-roundtrip-2\\.test$",
        "APINotes/yaml-roundtrip\\.test$",
        "AST/ByteCode/builtin-bit-cast-long-double\\.cpp$",
        "AST/ByteCode/builtin-bit-cast\\.cpp$",
        "AST/ByteCode/builtin-functions\\.cpp$",
        "AST/ByteCode/builtin-object-size\\.cpp$",
        "AST/ByteCode/c\\.c$",
        "AST/ByteCode/codegen\\.cpp$",
        "AST/ByteCode/complex\\.c$",
        "AST/ByteCode/complex\\.cpp$",
        "AST/ByteCode/const-eval\\.c$",
        "AST/ByteCode/const-fpfeatures\\.cpp$",
        "AST/ByteCode/constexpr\\.c$",
        "AST/ByteCode/cxx20\\.cpp$",
        "AST/ByteCode/fixed-point\\.cpp$",
        "AST/ByteCode/floats\\.cpp$",
        "AST/ByteCode/functions\\.cpp$",
        "AST/ByteCode/hlsl\\.hlsl$",
        "AST/ByteCode/intap\\.cpp$",
        "AST/ByteCode/invalid\\.cpp$",
        "AST/ByteCode/lambda\\.cpp$",
        "AST/ByteCode/literals\\.cpp$",
        "AST/ByteCode/memberpointers\\.cpp$",
        "AST/ByteCode/neon\\.c$",
        "AST/ByteCode/new-delete\\.cpp$",
        "AST/ByteCode/placement-new\\.cpp$",
        "AST/ByteCode/records\\.cpp$",
        "AST/ByteCode/spaceship\\.cpp$",
        "AST/ByteCode/unions\\.cpp$",
        "Analysis/dead-stores\\.c$",
        "Analysis/exploded-graph-rewriter/.*",
        "Analysis/scan-build/cxx-name\\.test$",
        "Analysis/scan-build/deduplication\\.test$",
        "Analysis/scan-build/exclude_directories\\.test$",
        "Analysis/scan-build/help\\.test$",
        "Analysis/scan-build/html_output\\.test$",
        "Analysis/scan-build/plist_html_output\\.test$",
        "Analysis/scan-build/plist_output\\.test$",
        "Analysis/scan-build/rebuild_index/rebuild_index\\.test$",
        "Analysis/scan-build/silence-core-checkers\\.test$",
        "Analysis/virtualcall-fixits\\.cpp$",
        "C/C23/n3018\\.c$",
        "ClangScanDeps/build-session-validation-outdated-module\\.c$",
        "ClangScanDeps/build-session-validation-relocated-modules\\.c$",
        "ClangScanDeps/diagnostics\\.c$",
        "ClangScanDeps/module.*",
        "ClangScanDeps/multiple-commands\\.c$",
        "ClangScanDeps/optimize-vfs-edgecases\\.m$",
        "ClangScanDeps/optimize-vfs-pch\\.m$",
        "ClangScanDeps/prebuilt-modules-in-stable-dirs\\.c$",
        "ClangScanDeps/tu-buffer\\.c$",
        "ClangScanDeps/visible-modules\\.c$",
        "ClangScanDeps/Wsystem-headers-in-module\\.c$",
        "CodeGen/builtins\\.c$",
        "CodeGen/compound-literal\\.c$",
        "CodeGenCUDA/device-const-var-linkage\\.cu$",
        "CodeGenCUDA/static-device-var-rdc\\.cu$",
        "CodeGenCXX/int64_uint64\\.cpp$",
        "CodeGenCXX/pointers-to-data-members\\.cpp$",
        "CodeGenCXX/template-arguments\\.cpp$",
        "CodeGenOpenCLCXX/constexpr\\.clcpp$",
        "Format/docs_updated\\.test$",
        "Frontend/rewrite-includes-bom\\.c$",
        "Interpreter/cxx20-modules\\.cppm$",
        "Modules/crash-vfs-umbrella-frameworks\\.m$",
        "Modules/double-quotes\\.m$",
        "Modules/framework-public-includes-private\\.m$",
        "Modules/implicit-module-header-maps\\.cpp$",
        "Preprocessor/embed_constexpr\\.c$",
        "Preprocessor/header-search-crash\\.c$",
        "Preprocessor/headermap-rel\\.c$",
        "Preprocessor/headermap-rel2\\.c$",
        "Preprocessor/include-header-missing-in-framework-with-headermap\\.c$",
        "Preprocessor/search-path-usage\\.m$",
        "PCH/cxx20-template-args\\.cpp$",
        "Sema/arithmetic-fence-builtin\\.c$",
        "Sema/atomic-expr\\.c$",
        "Sema/auto-type\\.c$",
        "Sema/builtin-expect-with-probability\\.cpp$",
        "Sema/c2x-auto\\.c$",
        "Sema/constant-builtins-fmax\\.cpp$",
        "Sema/constant-builtins-fmaximum-num\\.cpp$",
        "Sema/constant-builtins-fmin\\.cpp$",
        "Sema/constant-builtins-fminimum-num\\.cpp$",
        "Sema/constexpr\\.c$",
        "Sema/fp-eval-pragma-with-float-double_t-1\\.c$",
        "Sema/fp-eval-pragma-with-float-double_t-2\\.c$",
        "Sema/fp-eval-pragma-with-float-double_t-3\\.c$",
    ]

    def test_clang(self):
        bitbake("clang -c install")
        self.build_core_image()
        ctx, qemu = self.start_qemu_nfs()
        with ctx:
            cmd, guest_result, host_result = self.run_llvm_lit(
                "clang", extra_filter=self.CLANG_EXTRA_EXCLUDE
            )
            status, output = qemu.run(cmd, timeout=3600)
            if status != 0:
                raise AssertionError(f"llvm-lit failed for Clang: {output}")
            status, _ = qemu.run(f"cp {guest_result} {host_result}")
            if status != 0:
                raise AssertionError("Failed to copy Clang lit results back to host")

@OETestTag("toolchain-system")
@OETestTag("toolchain-user")
@OETestTag("runqemu")
class LLDSelfTestSystemEmulated(ClangFamilyBase):

    LLD_EXTRA_EXCLUDE = [
        "ELF/fill-trap\\.s$",
        "ELF/lto/cache-warnings\\.ll$",
        "ELF/oformat-binary-ttext\\.s$",
        "ELF/oformat-binary\\.s$",
        "ELF/partition-synthetic-sections\\.s$",
        "ELF/reproduce\\.s$",
    ]

    def test_lld(self):
        bitbake("lld -c install")
        self.build_core_image()
        ctx, qemu = self.start_qemu_nfs()
        with ctx:
            cmd, guest_result, host_result = self.run_llvm_lit(
                "lld", extra_filter=self.LLD_EXTRA_EXCLUDE
            )
            status, output = qemu.run(cmd, timeout=3600)
            if status != 0:
                raise AssertionError(f"llvm-lit failed for LLD: {output}")
            status, _ = qemu.run(f"cp {guest_result} {host_result}")
            if status != 0:
                raise AssertionError("Failed to copy LLD lit results back to host")
