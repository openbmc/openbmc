require libmpc.inc

DEPENDS = "gmp mpfr"

LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02"
SRC_URI = "${GNU_MIRROR}/mpc/mpc-${PV}.tar.gz"

SRC_URI[sha256sum] = "e90f2d99553a9c19911abdb4305bf8217106a957e3994436428572c8dfe8fda6"

S = "${WORKDIR}/mpc-${PV}"
BBCLASSEXTEND = "native nativesdk"

