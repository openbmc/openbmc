require gcc-common.inc

# Third digit in PV should be incremented after a minor release

PV = "8.2.0"

# BINV should be incremented to a revision after a minor gcc release

BINV = "8.2.0"

FILESEXTRAPATHS =. "${FILE_DIRNAME}/gcc-8.2:${FILE_DIRNAME}/gcc-8.2/backport:"

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

#RELEASE = "8.0.1-RC-20180427"
BASEURI ?= "${GNU_MIRROR}/gcc/gcc-${PV}/gcc-${PV}.tar.xz"
#SRCREV = "f7cf798b73fd1a07098f9a490deec1e2a36e0bed"
#BASEURI ?= "git://github.com/gcc-mirror/gcc;branch=gcc-6-branch;protocol=git"
#BASEURI ?= "http://mirrors.concertpass.com/gcc/snapshots/${RELEASE}/gcc-${RELEASE}.tar.xz"

SRC_URI = "\
           ${BASEURI} \
           file://0001-gcc-4.3.1-ARCH_FLAGS_FOR_TARGET.patch \
           file://0003-gcc-poison-system-directories.patch \
           file://0005-gcc-4.3.3-SYSROOT_CFLAGS_FOR_TARGET.patch \
           file://0006-64-bit-multilib-hack.patch \
           file://0007-optional-libstdc.patch \
           file://0008-gcc-disable-MASK_RELAX_PIC_CALLS-bit.patch \
           file://0009-COLLECT_GCC_OPTIONS.patch \
           file://0010-Use-the-defaults.h-in-B-instead-of-S-and-t-oe-in-B.patch \
           file://0011-fortran-cross-compile-hack.patch \
           file://0012-cpp-honor-sysroot.patch \
           file://0013-MIPS64-Default-to-N64-ABI.patch \
           file://0014-Define-GLIBC_DYNAMIC_LINKER-and-UCLIBC_DYNAMIC_LINKE.patch \
           file://0015-gcc-Fix-argument-list-too-long-error.patch \
           file://0016-Disable-sdt.patch \
           file://0017-libtool.patch \
           file://0018-gcc-armv4-pass-fix-v4bx-to-linker-to-support-EABI.patch \
           file://0019-Use-the-multilib-config-files-from-B-instead-of-usin.patch \
           file://0020-Avoid-using-libdir-from-.la-which-usually-points-to-.patch \
           file://0021-export-CPP.patch \
           file://0022-Disable-the-MULTILIB_OSDIRNAMES-and-other-multilib-o.patch \
           file://0023-Ensure-target-gcc-headers-can-be-included.patch \
           file://0024-gcc-4.8-won-t-build-with-disable-dependency-tracking.patch \
           file://0025-Don-t-search-host-directory-during-relink-if-inst_pr.patch \
           file://0026-Use-SYSTEMLIBS_DIR-replacement-instead-of-hardcoding.patch \
           file://0027-aarch64-Add-support-for-musl-ldso.patch \
           file://0028-libcc1-fix-libcc1-s-install-path-and-rpath.patch \
           file://0029-handle-sysroot-support-for-nativesdk-gcc.patch \
           file://0030-Search-target-sysroot-gcc-version-specific-dirs-with.patch \
           file://0031-Fix-various-_FOR_BUILD-and-related-variables.patch \
           file://0032-nios2-Define-MUSL_DYNAMIC_LINKER.patch \
           file://0033-Add-ssp_nonshared-to-link-commandline-for-musl-targe.patch \
           file://0034-libgcc-Add-knob-to-use-ldbl-128-on-ppc.patch \
           file://0035-Link-libgcc-using-LDFLAGS-not-just-SHLIB_LDFLAGS.patch \
           file://0036-libgcc_s-Use-alias-for-__cpu_indicator_init-instead-.patch \
           file://0037-sync-gcc-stddef.h-with-musl.patch \
           file://0038-fix-segmentation-fault-in-precompiled-header-generat.patch \
           file://0039-Fix-for-testsuite-failure.patch \
           file://0040-Re-introduce-spe-commandline-options.patch \
           file://0041-ARC-fix-spec-gen.patch \
           ${BACKPORTS} \
"
BACKPORTS = "\
"
SRC_URI[md5sum] = "4ab282f414676496483b3e1793d07862"
SRC_URI[sha256sum] = "196c3c04ba2613f893283977e6011b2345d1cd1af9abeac58e916b1aab3e0080"

S = "${TMPDIR}/work-shared/gcc-${PV}-${PR}/gcc-${PV}"
#S = "${TMPDIR}/work-shared/gcc-${PV}-${PR}/git"
#S = "${TMPDIR}/work-shared/gcc-${PV}-${PR}/gcc-${RELEASE}"
B = "${WORKDIR}/gcc-${PV}/build.${HOST_SYS}.${TARGET_SYS}"

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
