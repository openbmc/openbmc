require libmpc.inc

DEPENDS = "gmp mpfr"

LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02"
SRC_URI = "${GNU_MIRROR}/mpc/mpc-${PV}.tar.xz"

SRC_URI[sha256sum] = "91204cd32f164bd3b7c992d4a6a8ce6519511aadab30f78b6982d0bf8d73e931"

S = "${UNPACKDIR}/mpc-${PV}"
BBCLASSEXTEND = "native nativesdk"

