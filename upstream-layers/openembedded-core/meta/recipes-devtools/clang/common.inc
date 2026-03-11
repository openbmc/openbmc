FILESEXTRAPATHS =. "${FILE_DIRNAME}/clang:"

LIC_FILES_CHKSUM = "file://llvm/LICENSE.TXT;md5=${LLVMMD5SUM} \
                    file://clang/LICENSE.TXT;md5=${CLANGMD5SUM} \
"
LICENSE = "Apache-2.0-with-LLVM-exception"

# Snapshot
#RELEASE ?= "a69568efe6c4972e71af295c6577b3412dd57c22"
#BASEURI ?= "https://api.github.com/repos/llvm/llvm-project/tarball/${RELEASE};downloadfilename=llvm-${PV}-${RELEASE}.tar.gz"
#SOURCEDIR ?= "llvm-llvm-project-${@'${RELEASE}'[0:7]}"
#SRC_URI[sha256sum] = "aed4f66cd72867b36cc55a0588436933c3d58d3e61ca782b6bf57605d3fdcb09"

# GA Release
RELEASE ?= "${PV}"
BASEURI ?= "${LLVM_HTTP}/llvm-project/releases/download/llvmorg-${PV}/llvm-project-${PV}.src.tar.xz"
UPSTREAM_CHECK_URI = "${LLVM_HTTP}/llvm-project/releases/"
UPSTREAM_CHECK_REGEX = "releases/tag/llvmorg-?(?P<pver>\d+(\.\d+)+)"
SOURCEDIR ?= "llvm-project-${PV}.src"
SRC_URI[sha256sum] = "6898f963c8e938981e6c4a302e83ec5beb4630147c7311183cf61069af16333d"

SRC_URI = "\
    ${BASEURI} \
    file://0001-libcxxabi-Find-libunwind-headers-when-LIBCXXABI_LIBU.patch \
    file://0002-compiler-rt-support-a-new-embedded-linux-target.patch \
    file://0003-compiler-rt-Simplify-cross-compilation.-Don-t-use-na.patch \
    file://0004-llvm-TargetLibraryInfo-Undefine-libc-functions-if-th.patch \
    file://0005-llvm-allow-env-override-of-exe-and-libdir-path.patch \
    file://0006-clang-driver-Check-sysroot-for-ldso-path.patch \
    file://0007-clang-Driver-tools.cpp-Add-lssp_nonshared-on-musl.patch \
    file://0008-clang-Prepend-trailing-to-sysroot.patch \
    file://0009-clang-Look-inside-the-target-sysroot-for-compiler-ru.patch \
    file://0010-clang-Define-releative-gcc-installation-dir.patch \
    file://0011-clang-Add-lpthread-and-ldl-along-with-lunwind-for-st.patch \
    file://0012-Pass-PYTHON_EXECUTABLE-when-cross-compiling-for-nati.patch \
    file://0013-Check-for-atomic-double-intrinsics.patch \
    file://0014-cmake-Fix-configure-for-packages-using-find_package.patch \
    file://0015-clang-Fix-resource-dir-location-for-cross-toolchains.patch \
    file://0016-clang-driver-Add-dyld-prefix-when-checking-sysroot-f.patch \
    file://0017-clang-Use-python3-in-python-scripts.patch \
    file://0018-llvm-clang-Insert-anchor-for-adding-OE-distro-vendor.patch \
    file://0019-compiler-rt-Do-not-use-backtrace-APIs-on-non-glibc-l.patch \ 
    file://0020-clang-Fix-x86-triple-for-non-debian-multiarch-linux-.patch \
    file://0021-libunwind-Added-unw_backtrace-method.patch \
    file://0022-lldb-Link-with-libatomic-on-x86.patch \
    file://0023-compiler-rt-Enable-__int128-for-ppc32.patch \
    file://0024-llvm-Do-not-use-cmake-infra-to-detect-libzstd.patch \
    file://0025-compiler-rt-Fix-stat-struct-s-size-for-O32-ABI.patch \
    file://0026-compiler-rt-Undef-_TIME_BITS-along-with-_FILE_OFFSET.patch \
    file://0027-ToolChains-Gnu.cpp-ARMLibDirs-search-also-in-lib32.patch \
    file://0028-clang-llvm-Add-OE-specific-ABI-triple-for-N32-ABI.patch \
    file://0029-llvm-Add-libunwind.pc.in-and-llvm-config-scripts.patch \
    file://0030-scan-build-py-respect-LLVM_LIBDIR_SUFFIX-like-other-.patch \
    file://0031-compiler-rt-Do-not-pass-target-to-clang-compiler.patch \
    file://0032-clangd-Add-a-build-option-to-disable-building-dexp.patch \
    file://0033-AsmMatcherEmitter-sort-ClassInfo-lists-by-name-as-we.patch \
    file://0034-llvm-config-remove-LLVM_LDFLAGS-from-ldflags-output.patch \
    file://0035-openmp-Do-not-emit-date-and-time-into-generate-files.patch \
    file://0036-libunwind-Use-gcs-instead-of-gcs-target-attribute.patch \
    file://0037-compiler-rt-Exclude-sync_fetch_and_-for-any-pre-ARMv.patch \
    file://0038-compiler-rt-Hardcode-uptr-sptr-typedefs-on-Mips-Linu.patch \
    file://0001-libclc-allow-existing-prepare-builtins-in-standalone.patch \
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
