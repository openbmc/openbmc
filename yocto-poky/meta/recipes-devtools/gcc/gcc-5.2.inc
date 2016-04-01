require gcc-common.inc

# Third digit in PV should be incremented after a minor release

PV = "5.2.0"

#SNAP = "5-20150405"

# BINV should be incremented to a revision after a minor gcc release

BINV = "5.2.0"

FILESEXTRAPATHS =. "${FILE_DIRNAME}/gcc-5.2:${FILE_DIRNAME}/gcc-5.2/backport:"

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
#BASEURI = "http://www.netgull.com/gcc/snapshots/${SNAP}/gcc-${SNAP}.tar.bz2"
BASEURI = "${GNU_MIRROR}/gcc/gcc-${PV}/gcc-${PV}.tar.bz2"
SRC_URI[md5sum] = "a51bcfeb3da7dd4c623e27207ed43467"
SRC_URI[sha256sum] = "5f835b04b5f7dd4f4d2dc96190ec1621b8d89f2dc6f638f9f8bc1b1014ba8cad"

SRC_URI = "\
           ${BASEURI} \
           ${BACKPORTS} \
           file://0001-gcc-4.3.1-ARCH_FLAGS_FOR_TARGET.patch \
           file://0002-uclibc-conf.patch \
           file://0003-gcc-uclibc-locale-ctype_touplow_t.patch \
           file://0004-uclibc-locale.patch \
           file://0005-uclibc-locale-no__x.patch \
           file://0006-uclibc-locale-wchar_fix.patch \
           file://0007-uclibc-locale-update.patch \
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
           file://0019-libgcc-sjlj-check.patch \
           file://0020-cpp-honor-sysroot.patch \
           file://0021-MIPS64-Default-to-N64-ABI.patch \
           file://0022-Define-GLIBC_DYNAMIC_LINKER-and-UCLIBC_DYNAMIC_LINKE.patch \
           file://0023-gcc-Fix-argument-list-too-long-error.patch \
           file://0024-Disable-sdt.patch \
           file://0025-libtool.patch \
           file://0026-gcc-armv4-pass-fix-v4bx-to-linker-to-support-EABI.patch \
           file://0027-Use-the-multilib-config-files-from-B-instead-of-usin.patch \
           file://0028-Avoid-using-libdir-from-.la-which-usually-points-to-.patch \
           file://0029-export-CPP.patch \
           file://0030-Enable-SPE-AltiVec-generation-on-powepc-linux-target.patch \
           file://0031-Disable-the-MULTILIB_OSDIRNAMES-and-other-multilib-o.patch \
           file://0032-Ensure-target-gcc-headers-can-be-included.patch \
           file://0033-gcc-4.8-won-t-build-with-disable-dependency-tracking.patch \
           file://0034-Don-t-search-host-directory-during-relink-if-inst_pr.patch \
           file://0035-Dont-link-the-plugins-with-libgomp-explicitly.patch \
           file://0036-Use-SYSTEMLIBS_DIR-replacement-instead-of-hardcoding.patch \
           file://0037-pr65779.patch \
           file://0038-fix-g++-sysroot.patch \
           file://0039-libcc1-fix-libcc1-s-install-path-and-rpath.patch \
           file://0040-nativesdk-gcc-support.patch \
           file://0041-handle-target-sysroot-multilib.patch \
           file://0042-cxxflags-for-build.patch \
          "

BACKPORTS = ""
#S = "${TMPDIR}/work-shared/gcc-${PV}-${PR}/gcc-${SNAP}"
S = "${TMPDIR}/work-shared/gcc-${PV}-${PR}/gcc-${PV}"
B = "${WORKDIR}/gcc-${PV}/build.${HOST_SYS}.${TARGET_SYS}"

# Language Overrides
FORTRAN = ""
JAVA = ""

LTO = "--enable-lto"

EXTRA_OECONF_BASE = "\
    ${LTO} \
    --enable-libssp \
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
    --disable-libssp \
    --disable-libquadmath \
    --with-system-zlib \
    --disable-lto \
    --disable-plugin \
    --enable-decimal-float=no \
    --without-isl \
"

EXTRA_OECONF_append_libc-uclibc = " --disable-decimal-float "

EXTRA_OECONF_PATHS = "\
    --with-gxx-include-dir=/not/exist{target_includedir}/c++/${BINV} \
    --with-sysroot=/not/exist \
    --with-build-sysroot=${STAGING_DIR_TARGET} \
"
