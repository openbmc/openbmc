require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://0001-libc-compat.h-add-musl-workaround.patch \
           "

SRC_URI[sha256sum] = "5ba3d464d51c8c283550d507ffac3d10f7aec587b7c66b0ccb6950643646389e"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE:append = " CCOPTS='${CFLAGS}'"
