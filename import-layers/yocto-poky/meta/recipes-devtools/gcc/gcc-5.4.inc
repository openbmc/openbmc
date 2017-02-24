require gcc-common.inc

# Third digit in PV should be incremented after a minor release

PV = "5.4.0"

#SNAP = "5-20150405"

# BINV should be incremented to a revision after a minor gcc release

BINV = "5.4.0"

FILESEXTRAPATHS =. "${FILE_DIRNAME}/gcc-5.4:${FILE_DIRNAME}/gcc-5.4/backport:"

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
           file://0037-aarch64-Add-support-for-musl-ldso.patch \
           file://0038-fix-g-sysroot.patch \
           file://0039-libcc1-fix-libcc1-s-install-path-and-rpath.patch \
           file://0040-handle-sysroot-support-for-nativesdk-gcc.patch \
           file://0041-Search-target-sysroot-gcc-version-specific-dirs-with.patch \
           file://0042-Fix-various-_FOR_BUILD-and-related-variables.patch \
           file://0043-libstdc-Support-musl.patch \
           file://0044-Adding-mmusl-as-a-musl-libc-specifier-and-the-necess.patch \
           file://0045-Support-for-arm-linux-musl.patch \
           file://0046-Get-rid-of-ever-broken-fixincludes-on-musl.patch \
           file://0047-nios2-Define-MUSL_DYNAMIC_LINKER.patch \
           file://0048-ssp_nonshared.patch \
           file://0049-Disable-the-weak-reference-logic-in-gthr.h-for-os-ge.patch \
           file://0050-powerpc-pass-secure-plt-to-the-linker.patch \
           file://0051-Ignore-fdebug-prefix-map-in-producer-string-by-Danie.patch \
           file://0052-nios2-use-ret-with-r31.patch \
           file://0053-expr.c-PR-target-65358-Avoid-clobbering-partial-argu.patch \
           file://0054-support-ffile-prefix-map.patch \
           file://0055-Reuse-fdebug-prefix-map-to-replace-ffile-prefix-map.patch \
           file://0056-Enable-libc-provide-ssp-and-gcc_cv_target_dl_iterate.patch \
           file://0057-unwind-fix-for-musl.patch \
           file://0058-fdebug-prefix-map-support-to-remap-relative-path.patch \
           file://0059-libgcc-use-ldflags.patch \
"

BACKPORTS = ""

SRC_URI[md5sum] = "4c626ac2a83ef30dfb9260e6f59c2b30"
SRC_URI[sha256sum] = "608df76dec2d34de6558249d8af4cbee21eceddbcb580d666f7a5a583ca3303a"

UPSTREAM_CHECK_REGEX = "gcc-(?P<pver>5\.\d+\.\d+).tar"

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

EXTRA_OECONF_append_libc-uclibc = " --disable-decimal-float "

EXTRA_OECONF_PATHS = "\
    --with-gxx-include-dir=/not/exist{target_includedir}/c++/${BINV} \
    --with-sysroot=/not/exist \
    --with-build-sysroot=${STAGING_DIR_TARGET} \
"
