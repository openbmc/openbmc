require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://configure-cross.patch \
           file://0001-libc-compat.h-add-musl-workaround.patch \
          "

SRC_URI[md5sum] = "d22107b4d7cfb999eeb8ad8a0aec1124"
SRC_URI[sha256sum] = "df047302a39650ef832c07e8dab5df7a23218cd398bd310c8628e386161d20ba"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE_append = " CCOPTS='${CFLAGS}'"
