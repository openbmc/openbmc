require libmpc.inc

DEPENDS = "gmp mpfr"

LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02"
SRC_URI = "${GNU_MIRROR}/mpc/mpc-${PV}.tar.gz"

SRC_URI[sha256sum] = "ab642492f5cf882b74aa0cb730cd410a81edcdbec895183ce930e706c1c759b8"

S = "${WORKDIR}/mpc-${PV}"
BBCLASSEXTEND = "native nativesdk"

