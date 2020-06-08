require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://0001-libc-compat.h-add-musl-workaround.patch \
          "

SRC_URI[md5sum] = "9da0c352707c34b8b1fec3bf42fcfd09"
SRC_URI[sha256sum] = "1b5b0e25ce6e23da7526ea1da044e814ad85ba761b10dd29c2b027c056b04692"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE_append = " CCOPTS='${CFLAGS}'"
