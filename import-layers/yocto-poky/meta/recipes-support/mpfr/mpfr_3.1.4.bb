require mpfr.inc

LICENSE = "GPLv3 & LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
		    file://COPYING.LESSER;md5=6a6a8e020838b23406c81b19c1d46df6"
DEPENDS = "gmp"

SRC_URI = "http://www.mpfr.org/mpfr-${PV}/mpfr-${PV}.tar.xz \
           file://long-long-thumb.patch \
           "
SRC_URI[md5sum] = "064b2c18185038e404a401b830d59be8"
SRC_URI[sha256sum] = "761413b16d749c53e2bfd2b1dfaa3b027b0e793e404b90b5fbaeef60af6517f5"

UPSTREAM_CHECK_URI = "http://www.mpfr.org/mpfr-current/"

S = "${WORKDIR}/mpfr-${PV}"

BBCLASSEXTEND = "native nativesdk"
