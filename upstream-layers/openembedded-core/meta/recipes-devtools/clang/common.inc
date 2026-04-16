FILESEXTRAPATHS =. "${FILE_DIRNAME}/clang:"

LIC_FILES_CHKSUM = "file://llvm/LICENSE.TXT;md5=${LLVMMD5SUM} \
                    file://clang/LICENSE.TXT;md5=${CLANGMD5SUM} \
"
LICENSE = "Apache-2.0-with-LLVM-exception"

# Snapshot
#RELEASE ?= "108df0694cc12fa496f63faa3a8762e7cc1a3b37"
#BASEURI ?= "https://api.github.com/repos/llvm/llvm-project/tarball/${RELEASE};downloadfilename=llvm-${PV}-${RELEASE}.tar.gz"
#SOURCEDIR ?= "llvm-llvm-project-${@'${RELEASE}'[0:7]}"
#SRC_URI[sha256sum] = "f19bc6ed902e30413a8a034a024fae228ca57339b450d3682fd7437fb2573378"

# GA Release
RELEASE ?= "${PV}"
BASEURI ?= "${LLVM_HTTP}/llvm-project/releases/download/llvmorg-${PV}${VER_SUFFIX}/llvm-project-${PV}${VER_SUFFIX}.src.tar.xz"
UPSTREAM_CHECK_URI = "${LLVM_HTTP}/llvm-project/releases/"
UPSTREAM_CHECK_REGEX = "releases/tag/llvmorg-?(?P<pver>\d+(\.\d+)+)"
SOURCEDIR ?= "llvm-project-${PV}${VER_SUFFIX}.src"
SRC_URI[sha256sum] = "2488c33a959eafba1c44f253e5bbe7ac958eb53fa626298a3a5f4b87373767cd"

SRC_URI = "\
    ${BASEURI} \
    file://0001-libcxxabi-Find-libunwind-headers-when-LIBCXXABI_LIBU.patch \
    file://0002-compiler-rt-support-a-new-embedded-linux-target.patch \
    file://0003-compiler-rt-Simplify-cross-compilation.-Don-t-use-na.patch \
    file://0004-llvm-allow-env-override-of-exe-and-libdir-path.patch \
    file://0005-clang-driver-Check-sysroot-for-ldso-path.patch \
    file://0006-clang-Driver-tools.cpp-Add-lssp_nonshared-on-musl.patch \
    file://0007-clang-Prepend-trailing-to-sysroot.patch \
    file://0009-clang-Define-releative-gcc-installation-dir.patch \
    file://0010-clang-Add-lpthread-and-ldl-along-with-lunwind-for-st.patch \
    file://0011-Check-for-atomic-double-intrinsics.patch \
    file://0012-cmake-Fix-configure-for-packages-using-find_package.patch \
    file://0013-clang-Fix-resource-dir-location-for-cross-toolchains.patch \
    file://0014-clang-driver-Add-dyld-prefix-when-checking-sysroot-f.patch \
    file://0015-clang-Use-python3-in-python-scripts.patch \
    file://0016-llvm-clang-Insert-anchor-for-adding-OE-distro-vendor.patch \
    file://0017-compiler-rt-Do-not-use-backtrace-APIs-on-non-glibc-l.patch \
    file://0018-clang-Fix-x86-triple-for-non-debian-multiarch-linux-.patch \
    file://0019-libunwind-Added-unw_backtrace-method.patch \
    file://0020-lldb-Link-with-libatomic-on-x86.patch \
    file://0021-compiler-rt-Enable-__int128-for-ppc32.patch \
    file://0022-llvm-Do-not-use-cmake-infra-to-detect-libzstd.patch \
    file://0023-compiler-rt-Fix-stat-struct-s-size-for-O32-ABI.patch \
    file://0024-ToolChains-Gnu.cpp-ARMLibDirs-search-also-in-lib32.patch \
    file://0025-clang-llvm-Add-OE-specific-ABI-triple-for-N32-ABI.patch \
    file://0026-llvm-Add-libunwind.pc.in-and-llvm-config-scripts.patch \
    file://0027-scan-build-py-respect-LLVM_LIBDIR_SUFFIX-like-other-.patch \
    file://0028-compiler-rt-Do-not-pass-target-to-clang-compiler.patch \
    file://0029-AsmMatcherEmitter-sort-ClassInfo-lists-by-name-as-we.patch \
    file://0030-llvm-config-remove-LLVM_LDFLAGS-from-ldflags-output.patch \
    file://0031-compiler-rt-Exclude-sync_fetch_and_-for-any-pre-ARMv.patch \
    file://0032-compiler-rt-Hardcode-uptr-sptr-typedefs-on-Mips-Linu.patch \
    file://0034-Revert-libc-Remap-headers-in-the-debug-info-when-bui.patch \
    file://0035-Prevent-revisiting-block-when-searching-for-noreturn.patch \
    file://0036-Don-t-expose-LLVM_HAVE_OPT_VIEWER_MODULES-externally.patch \
    file://0037-clang-Only-build-clang-tblgen-if-it-is-actually-need.patch \
    file://0038-llvm-libgcc-Fix-symlink-path-for-libcc-when-LLVM_ENA.patch \
    file://0039-Rename-UNUSED-and-UNKNOWN-elements-of-OffloadArch-enum.patch \
    file://0040-libcxxabi-declare-__gnu_unwind_frame-in-cxa_personal.patch \
    file://0041-Consolidate-and-fix-sysroot-based-compiler-rt-search.patch \
"
# Fallback to no-PIE if not set
GCCPIE ??= ""

UNPACKDIR = "${TMPDIR}/work-shared/llvm-project-source-${PV}-${PR}/sources"
S = "${UNPACKDIR}/${SOURCEDIR}"
B ?= "${WORKDIR}/llvm-project-source-${PV}/build.${HOST_SYS}.${TARGET_SYS}"

# We need to ensure that for the shared work directory, the do_patch signatures match
# The real WORKDIR location isn't a dependency for the shared workdir.
src_patches[vardepsexclude] = "WORKDIR"
should_apply[vardepsexclude] += "PN"

CVE_PRODUCT = "llvm:llvm"
