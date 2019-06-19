require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://configure-cross.patch \
           file://0001-libc-compat.h-add-musl-workaround.patch \
          "

SRC_URI[md5sum] = "a2b8349abf4ae00e92155fda22de4d5e"
SRC_URI[sha256sum] = "dc5a980873eabf6b00c0be976b6e5562b1400d47d1d07d2ac35d5e5acbcf7bcf"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE_append = " CCOPTS='${CFLAGS}'"
