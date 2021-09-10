require libmpc.inc

DEPENDS = "gmp mpfr"

LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02"
SRC_URI = "${GNU_MIRROR}/mpc/mpc-${PV}.tar.gz"

SRC_URI[sha256sum] = "17503d2c395dfcf106b622dc142683c1199431d095367c6aacba6eec30340459"

S = "${WORKDIR}/mpc-${PV}"
BBCLASSEXTEND = "native nativesdk"

