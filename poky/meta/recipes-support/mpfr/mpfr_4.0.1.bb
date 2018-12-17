SUMMARY = "C library for multiple-precision floating-point computations with exact rounding"
HOMEPAGE = "http://www.mpfr.org/"
LICENSE = "LGPLv3+"
SECTION = "devel"

inherit autotools texinfo

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
		    file://COPYING.LESSER;md5=6a6a8e020838b23406c81b19c1d46df6"
DEPENDS = "gmp autoconf-archive"

SRC_URI = "http://www.mpfr.org/mpfr-${PV}/mpfr-${PV}.tar.xz \
           file://0001-Fix-obsolete-ARC-asm-constraints.patch"
SRC_URI[md5sum] = "b8dd19bd9bb1ec8831a6a582a7308073"
SRC_URI[sha256sum] = "67874a60826303ee2fb6affc6dc0ddd3e749e9bfcb4c8655e3953d0458a6e16e"

UPSTREAM_CHECK_URI = "http://www.mpfr.org/mpfr-current/"

BBCLASSEXTEND = "native nativesdk"
