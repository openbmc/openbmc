SUMMARY = "libtar, tar manipulating library"
DESCRIPTION = "libtar is a library for manipulating POSIX tar files"
HOMEPAGE = "http://www.feep.net/libtar"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=61cbac6719ae682ce6cd45b5c11e21af"

SRC_URI = "${DEBIAN_MIRROR}/main/libt/${BPN}/${BPN}_${PV}.orig.tar.gz \
           file://fix_libtool_sysroot.patch \
           file://0002-Do-not-strip-libtar.patch \
           file://0003-Fix-missing-prototype-compiler-warnings.patch \
           file://0004-Fix-invalid-memory-de-reference-issue.patch \
           file://0005-fix-file-descriptor-leaks-reported-by-cppcheck.patch \
           file://0006-fix-memleak-on-tar_open-failure.patch \
           file://0007-fix-memleaks-in-libtar-sample-program.patch \
           file://0008-decode-avoid-using-a-static-buffer-in-th_get_pathnam.patch \
           file://0009-Check-for-NULL-before-freeing-th_pathname.patch \
           file://0010-Added-stdlib.h-for-malloc-in-lib-decode.c.patch \
           file://0011-libtar-fix-programming-mistakes-detected-by-static-a.patch \
           file://CVE-2021-33643-CVE-2021-33644.patch \
           file://CVE-2021-33640-CVE-2021-33645-CVE-2021-33646.patch \
           file://CVE-2013-4420.patch \
           "

S = "${WORKDIR}/${BPN}"

SRC_URI[sha256sum] = "50f24c857a7ef1cb092e6508758b86d06f1188508f897f3e6b40c573e8879109"

inherit autotools-brokensep

PACKAGECONFIG ??= "zlib"
PACKAGECONFIG[zlib] = "--with-zlib,--without-zlib,zlib"

BBCLASSEXTEND += "native"
