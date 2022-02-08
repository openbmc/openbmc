require gcc-common.inc

# Third digit in PV should be incremented after a minor release

PV = "9.3.0"

# BINV should be incremented to a revision after a minor gcc release

BINV = "9.3.0"

FILESEXTRAPATHS =. "${FILE_DIRNAME}/gcc-9.3:${FILE_DIRNAME}/gcc-9.3/backport:"

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

CVE_CHECK_WHITELIST += "CVE-2019-15847"

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
           file://0006-COLLECT_GCC_OPTIONS.patch \
           file://0007-Use-the-defaults.h-in-B-instead-of-S-and-t-oe-in-B.patch \
           file://0008-fortran-cross-compile-hack.patch \
           file://0009-cpp-honor-sysroot.patch \
           file://0010-MIPS64-Default-to-N64-ABI.patch \
           file://0011-Define-GLIBC_DYNAMIC_LINKER-and-UCLIBC_DYNAMIC_LINKE.patch \
           file://0012-gcc-Fix-argument-list-too-long-error.patch \
           file://0013-Disable-sdt.patch \
           file://0014-libtool.patch \
           file://0015-gcc-armv4-pass-fix-v4bx-to-linker-to-support-EABI.patch \
           file://0016-Use-the-multilib-config-files-from-B-instead-of-usin.patch \
           file://0017-Avoid-using-libdir-from-.la-which-usually-points-to-.patch \
           file://0018-export-CPP.patch \
           file://0019-Ensure-target-gcc-headers-can-be-included.patch \
           file://0020-gcc-4.8-won-t-build-with-disable-dependency-tracking.patch \
           file://0021-Don-t-search-host-directory-during-relink-if-inst_pr.patch \
           file://0022-Use-SYSTEMLIBS_DIR-replacement-instead-of-hardcoding.patch \
           file://0023-aarch64-Add-support-for-musl-ldso.patch \
           file://0024-libcc1-fix-libcc1-s-install-path-and-rpath.patch \
           file://0025-handle-sysroot-support-for-nativesdk-gcc.patch \
           file://0026-Search-target-sysroot-gcc-version-specific-dirs-with.patch \
           file://0027-Fix-various-_FOR_BUILD-and-related-variables.patch \
           file://0028-nios2-Define-MUSL_DYNAMIC_LINKER.patch \
           file://0029-Add-ssp_nonshared-to-link-commandline-for-musl-targe.patch \
           file://0030-ldbl128-config.patch \
           file://0031-Link-libgcc-using-LDFLAGS-not-just-SHLIB_LDFLAGS.patch \
           file://0032-libgcc_s-Use-alias-for-__cpu_indicator_init-instead-.patch \
           file://0033-sync-gcc-stddef.h-with-musl.patch \
           file://0034-fix-segmentation-fault-in-precompiled-header-generat.patch \
           file://0035-Fix-for-testsuite-failure.patch \
           file://0036-Re-introduce-spe-commandline-options.patch \
           file://0037-CVE-2019-14250-Check-zero-value-in-simple_object_elf.patch \
           file://0038-gentypes-genmodes-Do-not-use-__LINE__-for-maintainin.patch \
           file://0039-process_alt_operands-Don-t-match-user-defined-regs-o.patch \
           file://0040-fix-missing-dependencies-for-selftests.patch \
           file://0001-aarch64-New-Straight-Line-Speculation-SLS-mitigation.patch \
           file://0002-aarch64-Introduce-SLS-mitigation-for-RET-and-BR-inst.patch \
           file://0003-aarch64-Mitigate-SLS-for-BLR-instruction.patch \
           file://0001-Backport-fix-for-PR-tree-optimization-97236-fix-bad-.patch \
"
S = "${TMPDIR}/work-shared/gcc-${PV}-${PR}/gcc-${PV}"
SRC_URI[sha256sum] = "71e197867611f6054aa1119b13a0c0abac12834765fe2d81f35ac57f84f742d1"
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

# Is a binutils 2.26 issue, not gcc
CVE_CHECK_WHITELIST += "CVE-2021-37322"
