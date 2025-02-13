SUMMARY = "C library for multiple-precision floating-point computations with exact rounding"
DESCRIPTION = "The GNU Multiple Precision Floating-Point Reliable Library (GNU MPFR) is a GNU portable C library for arbitrary-precision binary floating-point computation with correct rounding, based on GNU Multi-Precision Library. MPFR's computation is both efficient and has a well-defined semantics: the functions are completely specified on all the possible operands and the results do not depend on the platform."
HOMEPAGE = "https://www.mpfr.org/"
LICENSE = "LGPL-3.0-or-later"
SECTION = "devel"

inherit autotools texinfo

LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464 \
                    file://COPYING.LESSER;md5=3000208d539ec061b899bce1d9ce9404 \
                    "
CVE_PRODUCT = "gnu_mpfr"

DEPENDS = "gmp autoconf-archive-native"

SRC_URI = "https://www.mpfr.org/mpfr-${PV}/mpfr-${PV}.tar.xz"
SRC_URI += "file://0001-include-math.h-to-use-predefined-_Float128-definitio.patch"
SRC_URI[sha256sum] = "277807353a6726978996945af13e52829e3abd7a9a5b7fb2793894e18f1fcbb2"

UPSTREAM_CHECK_URI = "http://www.mpfr.org/mpfr-current/"

BBCLASSEXTEND = "native nativesdk"
