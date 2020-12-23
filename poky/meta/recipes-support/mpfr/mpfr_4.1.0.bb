SUMMARY = "C library for multiple-precision floating-point computations with exact rounding"
HOMEPAGE = "https://www.mpfr.org/"
LICENSE = "LGPLv3+"
SECTION = "devel"

inherit autotools texinfo

LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464 \
                    file://COPYING.LESSER;md5=3000208d539ec061b899bce1d9ce9404 \
                    "
DEPENDS = "gmp autoconf-archive"

SRC_URI = "https://www.mpfr.org/mpfr-${PV}/mpfr-${PV}.tar.xz"
SRC_URI[sha256sum] = "0c98a3f1732ff6ca4ea690552079da9c597872d30e96ec28414ee23c95558a7f"

UPSTREAM_CHECK_URI = "http://www.mpfr.org/mpfr-current/"

BBCLASSEXTEND = "native nativesdk"
