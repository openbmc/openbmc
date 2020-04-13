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
SRC_URI[md5sum] = "320fbc4463d4c8cb1e566929d8adc4f8"
SRC_URI[sha256sum] = "1d3be708604eae0e42d578ba93b390c2a145f17743a744d8f3f8c2ad5855a38a"

UPSTREAM_CHECK_URI = "http://www.mpfr.org/mpfr-current/"

BBCLASSEXTEND = "native nativesdk"
