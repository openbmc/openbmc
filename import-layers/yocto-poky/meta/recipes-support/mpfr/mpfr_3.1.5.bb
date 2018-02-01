require mpfr.inc

LICENSE = "GPLv3 & LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
		    file://COPYING.LESSER;md5=6a6a8e020838b23406c81b19c1d46df6"
DEPENDS = "gmp"

SRC_URI = "http://www.mpfr.org/mpfr-${PV}/mpfr-${PV}.tar.xz \
           file://long-long-thumb.patch \
           "
SRC_URI[md5sum] = "c4ac246cf9795a4491e7766002cd528f"
SRC_URI[sha256sum] = "015fde82b3979fbe5f83501986d328331ba8ddf008c1ff3da3c238f49ca062bc"

UPSTREAM_CHECK_URI = "http://www.mpfr.org/mpfr-current/"

S = "${WORKDIR}/mpfr-${PV}"

BBCLASSEXTEND = "native nativesdk"
