require gcc-common.inc

# Third digit in PV should be incremented after a minor release

PV = "9.1.0"

# BINV should be incremented to a revision after a minor gcc release

BINV = "9.1.0"

FILESEXTRAPATHS =. "${FILE_DIRNAME}/gcc-9.1:${FILE_DIRNAME}/gcc-9.1/backport:"

DEPENDS =+ "mpfr gmp libmpc zlib flex-native"
NATIVEDEPS = "mpfr-native gmp-native libmpc-native zlib-native flex-native"

LICENSE = "GPL-3.0-with-GCC-exception & GPLv3"

LIC_FILES_CHKSUM = "\
    file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
    file://COPYING3;md5=d32239bcb673463ab874e80d47fae504 \
    file://COPYING3.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
    file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
    file://COPYING.RUNTIME;md5=fe60d87048567d4fe8c8a0ed2448bcc8 \
"

BASEURI ?= "${GNU_MIRROR}/gcc/gcc-${PV}/gcc-${PV}.tar.xz"
#RELEASE ?= "5a5ca2d"
#BASEURI ?= "https://repo.or.cz/official-gcc.git/snapshot/${RELEASE}.tar.gz;downloadfilename=gcc-${RELEASE}.tar.gz"
SRC_URI = "\
           ${BASEURI} \
           file://0001-gcc-4.3.1-ARCH_FLAGS_FOR_TARGET.patch \
           file://0002-gcc-poison-system-directories.patch \
           file://0003-gcc-4.3.3-SYSROOT_CFLAGS_FOR_TARGET.patch \
           file://0004-64-bit-multilib-hack.patch \
           file://0005-optional-libstdc.patch \
           file://0007-COLLECT_GCC_OPTIONS.patch \
           file://0008-Use-the-defaults.h-in-B-instead-of-S-and-t-oe-in-B.patch \
           file://0009-fortran-cross-compile-hack.patch \
           file://0010-cpp-honor-sysroot.patch \
           file://0011-MIPS64-Default-to-N64-ABI.patch \
           file://0012-Define-GLIBC_DYNAMIC_LINKER-and-UCLIBC_DYNAMIC_LINKE.patch \
           file://0013-gcc-Fix-argument-list-too-long-error.patch \
           file://0014-Disable-sdt.patch \
           file://0015-libtool.patch \
           file://0016-gcc-armv4-pass-fix-v4bx-to-linker-to-support-EABI.patch \
           file://0017-Use-the-multilib-config-files-from-B-instead-of-usin.patch \
           file://0018-Avoid-using-libdir-from-.la-which-usually-points-to-.patch \
           file://0019-export-CPP.patch \
           file://0020-Ensure-target-gcc-headers-can-be-included.patch \
           file://0021-gcc-4.8-won-t-build-with-disable-dependency-tracking.patch \
           file://0022-Don-t-search-host-directory-during-relink-if-inst_pr.patch \
           file://0023-Use-SYSTEMLIBS_DIR-replacement-instead-of-hardcoding.patch \
           file://0024-aarch64-Add-support-for-musl-ldso.patch \
           file://0025-libcc1-fix-libcc1-s-install-path-and-rpath.patch \
           file://0026-handle-sysroot-support-for-nativesdk-gcc.patch \
           file://0027-Search-target-sysroot-gcc-version-specific-dirs-with.patch \
           file://0028-Fix-various-_FOR_BUILD-and-related-variables.patch \
           file://0029-nios2-Define-MUSL_DYNAMIC_LINKER.patch \
           file://0030-Add-ssp_nonshared-to-link-commandline-for-musl-targe.patch \
           file://0031-libgcc-Add-knob-to-use-ldbl-128-on-ppc.patch \
           file://0032-Link-libgcc-using-LDFLAGS-not-just-SHLIB_LDFLAGS.patch \
           file://0033-libgcc_s-Use-alias-for-__cpu_indicator_init-instead-.patch \
           file://0034-sync-gcc-stddef.h-with-musl.patch \
           file://0035-fix-segmentation-fault-in-precompiled-header-generat.patch \
           file://0036-Fix-for-testsuite-failure.patch \
           file://0037-Re-introduce-spe-commandline-options.patch \
"
SRC_URI[md5sum] = "6069ae3737cf02bf2cb44a391ef0e937"
SRC_URI[sha256sum] = "79a66834e96a6050d8fe78db2c3b32fb285b230b855d0a66288235bc04b327a0"
S = "${TMPDIR}/work-shared/gcc-${PV}-${PR}/gcc-${PV}"
# For dev release snapshotting
#S = "${TMPDIR}/work-shared/gcc-${PV}-${PR}/official-gcc-${RELEASE}"
#B = "${WORKDIR}/gcc-${PV}/build.${HOST_SYS}.${TARGET_SYS}"

# Language Overrides
FORTRAN = ""
JAVA = ""

LTO = "--enable-lto"
SSP ?= "--disable-libssp"
SSP_mingw32 = "--enable-libssp"

EXTRA_OECONF_BASE = "\
    ${LTO} \
    ${SSP} \
    --enable-libitm \
    --disable-bootstrap \
    --disable-libmudflap \
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
    --disable-libmudflap \
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
