require gcc-common.inc

# Third digit in PV should be incremented after a minor release

PV = "13.4.0"

# BINV should be incremented to a revision after a minor gcc release

BINV = "13.4.0"

FILESEXTRAPATHS =. "${FILE_DIRNAME}/gcc:${FILE_DIRNAME}/gcc/backport:"

DEPENDS =+ "mpfr gmp libmpc zlib zstd flex-native"
NATIVEDEPS = "mpfr-native gmp-native libmpc-native zlib-native flex-native zstd-native"

LICENSE = "GPL-3.0-with-GCC-exception & GPL-3.0-only"

LIC_FILES_CHKSUM = "\
    file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
    file://COPYING3;md5=d32239bcb673463ab874e80d47fae504 \
    file://COPYING3.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
    file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
    file://COPYING.RUNTIME;md5=fe60d87048567d4fe8c8a0ed2448bcc8 \
"
# snapshot from git
#RELEASE ?= "9b6bf076c11cba0f9ccdace63e8b4044b1a858ea"
#BASEURI ?= "https://git.linaro.org/toolchain/gcc.git/snapshot/gcc-${RELEASE}.tar.gz"
#SOURCEDIR = "gcc-${RELEASE}"
#BASEURI ?= "https://repo.or.cz/official-gcc.git/snapshot/${RELEASE}.tar.gz;downloadfilename=gcc-${PV}-${RELEASE}.tar.gz"
#SOURCEDIR ?= "official-gcc-${@'${RELEASE}'[0:7]}"

# from snapshot
#RELEASE ?= "13-20230520"
#SOURCEDIR ?= "gcc-${RELEASE}"
#BASEURI ?= "https://gcc.gnu.org/pub/gcc/snapshots/${RELEASE}/gcc-${RELEASE}.tar.xz"

# official release
RELEASE ?= "${PV}"
BASEURI ?= "${GNU_MIRROR}/gcc/gcc-${PV}/gcc-${PV}.tar.xz"
SOURCEDIR ?= "gcc-${PV}"

SRC_URI = "${BASEURI} \
           file://0001-gcc-4.3.1-ARCH_FLAGS_FOR_TARGET.patch \
           file://0002-gcc-poison-system-directories.patch \
           file://0003-64-bit-multilib-hack.patch \
           file://0004-Pass-CXXFLAGS_FOR_BUILD-in-a-couple-of-places-to-avo.patch \
           file://0005-Use-the-defaults.h-in-B-instead-of-S-and-t-oe-in-B.patch \
           file://0006-cpp-honor-sysroot.patch \
           file://0007-Define-GLIBC_DYNAMIC_LINKER-and-UCLIBC_DYNAMIC_LINKE.patch \
           file://0008-libtool.patch \
           file://0009-gcc-armv4-pass-fix-v4bx-to-linker-to-support-EABI.patch \
           file://0010-Use-the-multilib-config-files-from-B-instead-of-usin.patch \
           file://0011-aarch64-Fix-include-paths-when-S-B.patch \
           file://0012-Avoid-using-libdir-from-.la-which-usually-points-to-.patch \
           file://0013-Ensure-target-gcc-headers-can-be-included.patch \
           file://0014-Don-t-search-host-directory-during-relink-if-inst_pr.patch \
           file://0015-libcc1-fix-libcc1-s-install-path-and-rpath.patch \
           file://0016-handle-sysroot-support-for-nativesdk-gcc.patch \
           file://0017-Search-target-sysroot-gcc-version-specific-dirs-with.patch \
           file://0018-Add-ssp_nonshared-to-link-commandline-for-musl-targe.patch \
           file://0019-Re-introduce-spe-commandline-options.patch \
           file://0020-libgcc_s-Use-alias-for-__cpu_indicator_init-instead-.patch \
           file://0021-gentypes-genmodes-Do-not-use-__LINE__-for-maintainin.patch \
           file://0022-libatomic-Do-not-enforce-march-on-aarch64.patch \
           file://0023-Fix-install-path-of-linux64.h.patch \
           file://0024-Avoid-hardcoded-build-paths-into-ppc-libgcc.patch \
           file://0025-gcc-testsuite-tweaks-for-mips-OE.patch \
           file://0027-Fix-gcc-vect-module-testcases.patch \
"
SRC_URI[sha256sum] = "9c4ce6dbb040568fdc545588ac03c5cbc95a8dbf0c7aa490170843afb59ca8f5"

S = "${TMPDIR}/work-shared/gcc-${PV}-${PR}/${SOURCEDIR}"
B = "${WORKDIR}/gcc-${PV}/build.${HOST_SYS}.${TARGET_SYS}"

# Language Overrides
FORTRAN = ""
JAVA = ""

SSP ?= "--disable-libssp"
SSP:mingw32 = "--enable-libssp"

EXTRA_OECONF_BASE = "\
    ${SSP} \
    --enable-libitm \
    --enable-lto \
    --disable-bootstrap \
    --with-system-zlib \
    ${@'--with-linker-hash-style=${LINKER_HASH_STYLE}' if '${LINKER_HASH_STYLE}' else ''} \
    --enable-linker-build-id \
    --with-ppl=no \
    --with-cloog=no \
    --enable-checking=release \
    --enable-cheaders=c_global \
    --without-isl \
"

EXTRA_OECONF_INITIAL = "\
    --disable-libgomp \
    --disable-libitm \
    --disable-libquadmath \
    --with-system-zlib \
    --disable-lto \
    --disable-plugin \
    --enable-linker-build-id \
    --enable-decimal-float=no \
    --without-isl \
    --disable-libssp \
"

EXTRA_OECONF_PATHS = "\
    --with-gxx-include-dir=/not/exist{target_includedir}/c++/${BINV} \
    --with-sysroot=/not/exist \
    --with-build-sysroot=${STAGING_DIR_TARGET} \
"

CVE_STATUS[CVE-2021-37322] = "cpe-incorrect: Is a binutils 2.26 issue, not gcc"
CVE_STATUS[CVE-2023-4039] = "fixed-version: Fixed via CVE-2023-4039.patch included here. Set the status explictly to deal with all recipes that share the gcc-source"
