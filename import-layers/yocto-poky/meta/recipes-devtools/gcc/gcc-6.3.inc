require gcc-common.inc

# Third digit in PV should be incremented after a minor release

PV = "6.3.0"

# BINV should be incremented to a revision after a minor gcc release

BINV = "6.3.0"

FILESEXTRAPATHS =. "${FILE_DIRNAME}/gcc-6.3:${FILE_DIRNAME}/gcc-6.3/backport:"

DEPENDS =+ "mpfr gmp libmpc zlib"
NATIVEDEPS = "mpfr-native gmp-native libmpc-native zlib-native"

LICENSE = "GPL-3.0-with-GCC-exception & GPLv3"

LIC_FILES_CHKSUM = "\
    file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
    file://COPYING3;md5=d32239bcb673463ab874e80d47fae504 \
    file://COPYING3.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
    file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
    file://COPYING.RUNTIME;md5=fe60d87048567d4fe8c8a0ed2448bcc8 \
"


BASEURI ?= "${GNU_MIRROR}/gcc/gcc-${PV}/gcc-${PV}.tar.bz2"
#SRCREV = "bd9a826d5448db11d29d2ec5884e7e679066f140"
#BASEURI ?= "git://github.com/gcc-mirror/gcc;branch=gcc-6-branch;protocol=git"
#BASEURI ?= "ftp://sourceware.org/pub/gcc/snapshots/6.2.0-RC-20160815/gcc-6.2.0-RC-20160815.tar.bz2"

SRC_URI = "\
           ${BASEURI} \
           file://0001-gcc-4.3.1-ARCH_FLAGS_FOR_TARGET.patch \
           file://0008-missing-execinfo_h.patch \
           file://0009-c99-snprintf.patch \
           file://0010-gcc-poison-system-directories.patch \
           file://0011-gcc-poison-dir-extend.patch \
           file://0012-gcc-4.3.3-SYSROOT_CFLAGS_FOR_TARGET.patch \
           file://0013-64-bit-multilib-hack.patch \
           file://0014-optional-libstdc.patch \
           file://0015-gcc-disable-MASK_RELAX_PIC_CALLS-bit.patch \
           file://0016-COLLECT_GCC_OPTIONS.patch \
           file://0017-Use-the-defaults.h-in-B-instead-of-S-and-t-oe-in-B.patch \
           file://0018-fortran-cross-compile-hack.patch \
           file://0019-cpp-honor-sysroot.patch \
           file://0020-MIPS64-Default-to-N64-ABI.patch \
           file://0021-Define-GLIBC_DYNAMIC_LINKER-and-UCLIBC_DYNAMIC_LINKE.patch \
           file://0022-gcc-Fix-argument-list-too-long-error.patch \
           file://0023-Disable-sdt.patch \
           file://0024-libtool.patch \
           file://0025-gcc-armv4-pass-fix-v4bx-to-linker-to-support-EABI.patch \
           file://0026-Use-the-multilib-config-files-from-B-instead-of-usin.patch \
           file://0027-Avoid-using-libdir-from-.la-which-usually-points-to-.patch \
           file://0028-export-CPP.patch \
           file://0029-Enable-SPE-AltiVec-generation-on-powepc-linux-target.patch \
           file://0030-Disable-the-MULTILIB_OSDIRNAMES-and-other-multilib-o.patch \
           file://0031-Ensure-target-gcc-headers-can-be-included.patch \
           file://0032-gcc-4.8-won-t-build-with-disable-dependency-tracking.patch \
           file://0033-Don-t-search-host-directory-during-relink-if-inst_pr.patch \
           file://0034-Use-SYSTEMLIBS_DIR-replacement-instead-of-hardcoding.patch \
           file://0035-aarch64-Add-support-for-musl-ldso.patch \
           file://0036-libcc1-fix-libcc1-s-install-path-and-rpath.patch \
           file://0037-handle-sysroot-support-for-nativesdk-gcc.patch \
           file://0038-Search-target-sysroot-gcc-version-specific-dirs-with.patch \
           file://0039-Fix-various-_FOR_BUILD-and-related-variables.patch \
           file://0040-nios2-Define-MUSL_DYNAMIC_LINKER.patch \
           file://0041-ssp_nonshared.patch \
           file://0042-gcc-libcpp-support-ffile-prefix-map-old-new.patch \
           file://0043-Reuse-fdebug-prefix-map-to-replace-ffile-prefix-map.patch \
           file://0044-gcc-final.c-fdebug-prefix-map-support-to-remap-sourc.patch \
           file://0045-libgcc-Add-knob-to-use-ldbl-128-on-ppc.patch \
           file://0046-Link-libgcc-using-LDFLAGS-not-just-SHLIB_LDFLAGS.patch \
           file://0047-libgcc_s-Use-alias-for-__cpu_indicator_init-instead-.patch \
           file://0048-sync-gcc-stddef.h-with-musl.patch \
           file://0054_all_nopie-all-flags.patch \
           file://0055-unwind_h-glibc26.patch \
           file://0056-LRA-PR70904-relax-the-restriction-on-subreg-reload-f.patch \
           ${BACKPORTS} \
"
BACKPORTS = "\
           file://CVE-2016-6131.patch \
           file://ubsan-fix-check-empty-string.patch \
"
SRC_URI[md5sum] = "677a7623c7ef6ab99881bc4e048debb6"
SRC_URI[sha256sum] = "f06ae7f3f790fbf0f018f6d40e844451e6bc3b7bc96e128e63b09825c1f8b29f"

S = "${TMPDIR}/work-shared/gcc-${PV}-${PR}/gcc-${PV}"
#S = "${TMPDIR}/work-shared/gcc-${PV}-${PR}/git"
B = "${WORKDIR}/gcc-${PV}/build.${HOST_SYS}.${TARGET_SYS}"

# Language Overrides
FORTRAN = ""
JAVA = ""

LTO = "--enable-lto"

EXTRA_OECONF_BASE = "\
    ${LTO} \
    --enable-libssp \
    --enable-libitm \
    --disable-bootstrap \
    --disable-libmudflap \
    --with-system-zlib \
    --with-linker-hash-style=${LINKER_HASH_STYLE} \
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
    --enable-decimal-float=no \
    --without-isl \
    gcc_cv_libc_provides_ssp=yes \
"

EXTRA_OECONF_PATHS = "\
    --with-gxx-include-dir=/not/exist{target_includedir}/c++/${BINV} \
    --with-sysroot=/not/exist \
    --with-build-sysroot=${STAGING_DIR_TARGET} \
"
