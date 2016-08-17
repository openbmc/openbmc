require mpfr.inc

LICENSE = "GPLv3 & LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
		    file://COPYING.LESSER;md5=6a6a8e020838b23406c81b19c1d46df6"
DEPENDS = "gmp"

SRC_URI = "http://www.mpfr.org/mpfr-${PV}/mpfr-${PV}.tar.xz \
           file://long-long-thumb.patch \
           "

SRC_URI[md5sum] = "6969398cd2fbc56a6af570b5273c56a9"
SRC_URI[sha256sum] = "6835a08bd992c8257641791e9a6a2b35b02336c8de26d0a8577953747e514a16"

UPSTREAM_CHECK_URI = "http://www.mpfr.org/mpfr-current/"

S = "${WORKDIR}/mpfr-${PV}"

BBCLASSEXTEND = "native nativesdk"
