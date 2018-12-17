require gcc-common.inc

# Third digit in PV should be incremented after a minor release

PV = "7.3.0"

# BINV should be incremented to a revision after a minor gcc release

BINV = "7.3.0"

FILESEXTRAPATHS =. "${FILE_DIRNAME}/gcc-7.3:${FILE_DIRNAME}/gcc-7.3/backport:"

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

#RELEASE = "7-20170504"
BASEURI ?= "${GNU_MIRROR}/gcc/gcc-${PV}/gcc-${PV}.tar.xz"
#SRCREV = "f7cf798b73fd1a07098f9a490deec1e2a36e0bed"
#BASEURI ?= "git://github.com/gcc-mirror/gcc;branch=gcc-6-branch;protocol=git"
#BASEURI ?= "http://mirrors.concertpass.com/gcc/snapshots/${RELEASE}/gcc-${RELEASE}.tar.bz2"

SRC_URI = "\
           ${BASEURI} \
           file://0001-gcc-4.3.1-ARCH_FLAGS_FOR_TARGET.patch \
           file://0009-gcc-poison-system-directories.patch \
           file://0010-gcc-poison-dir-extend.patch \
           file://0011-gcc-4.3.3-SYSROOT_CFLAGS_FOR_TARGET.patch \
           file://0012-64-bit-multilib-hack.patch \
           file://0013-optional-libstdc.patch \
           file://0014-gcc-disable-MASK_RELAX_PIC_CALLS-bit.patch \
           file://0015-COLLECT_GCC_OPTIONS.patch \
           file://0016-Use-the-defaults.h-in-B-instead-of-S-and-t-oe-in-B.patch \
           file://0017-fortran-cross-compile-hack.patch \
           file://0018-cpp-honor-sysroot.patch \
           file://0019-MIPS64-Default-to-N64-ABI.patch \
           file://0020-Define-GLIBC_DYNAMIC_LINKER-and-UCLIBC_DYNAMIC_LINKE.patch \
           file://0021-gcc-Fix-argument-list-too-long-error.patch \
           file://0022-Disable-sdt.patch \
           file://0023-libtool.patch \
           file://0024-gcc-armv4-pass-fix-v4bx-to-linker-to-support-EABI.patch \
           file://0025-Use-the-multilib-config-files-from-B-instead-of-usin.patch \
           file://0026-Avoid-using-libdir-from-.la-which-usually-points-to-.patch \
           file://0027-export-CPP.patch \
           file://0028-Enable-SPE-AltiVec-generation-on-powepc-linux-target.patch \
           file://0029-Disable-the-MULTILIB_OSDIRNAMES-and-other-multilib-o.patch \
           file://0030-Ensure-target-gcc-headers-can-be-included.patch \
           file://0031-gcc-4.8-won-t-build-with-disable-dependency-tracking.patch \
           file://0032-Don-t-search-host-directory-during-relink-if-inst_pr.patch \
           file://0033-Use-SYSTEMLIBS_DIR-replacement-instead-of-hardcoding.patch \
           file://0034-aarch64-Add-support-for-musl-ldso.patch \
           file://0035-libcc1-fix-libcc1-s-install-path-and-rpath.patch \
           file://0036-handle-sysroot-support-for-nativesdk-gcc.patch \
           file://0037-Search-target-sysroot-gcc-version-specific-dirs-with.patch \
           file://0038-Fix-various-_FOR_BUILD-and-related-variables.patch \
           file://0039-nios2-Define-MUSL_DYNAMIC_LINKER.patch \
           file://0040-Add-ssp_nonshared-to-link-commandline-for-musl-targe.patch \
           file://0041-gcc-libcpp-support-ffile-prefix-map-old-new.patch \
           file://0042-Reuse-fdebug-prefix-map-to-replace-ffile-prefix-map.patch \
           file://0043-gcc-final.c-fdebug-prefix-map-support-to-remap-sourc.patch \
           file://0044-libgcc-Add-knob-to-use-ldbl-128-on-ppc.patch \
           file://0045-Link-libgcc-using-LDFLAGS-not-just-SHLIB_LDFLAGS.patch \
           file://0047-sync-gcc-stddef.h-with-musl.patch \
           file://0048-gcc-Enable-static-PIE.patch \
           file://0049-gcc-override-TARGET_LIBC_PROVIDES_SSP.patch \
           file://fix-segmentation-fault-precompiled-hdr.patch \
           file://no-sse-fix-test-case-failures.patch \
           ${BACKPORTS} \
"
BACKPORTS = "\
           file://0001-Fix-internal-compiler-error-in-testcase.patch \
           file://0001-PR-rtl-optimization-83030.patch \
           file://0001-Fix-ppc64le-build-Partial-backport-r256656.patch \
           file://0001-PR-c-80290-memory-hog-with-std-pair.patch \
"

SRC_URI[md5sum] = "be2da21680f27624f3a87055c4ba5af2"
SRC_URI[sha256sum] = "832ca6ae04636adbb430e865a1451adf6979ab44ca1c8374f61fba65645ce15c"

S = "${TMPDIR}/work-shared/gcc-${PV}-${PR}/gcc-${PV}"
#S = "${TMPDIR}/work-shared/gcc-${PV}-${PR}/git"
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
