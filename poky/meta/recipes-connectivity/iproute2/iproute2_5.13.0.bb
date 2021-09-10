require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://0001-libc-compat.h-add-musl-workaround.patch \
           "

SRC_URI[sha256sum] = "72a2e53774cac9e65f7b617deebb2059f87e8960d6e9713e4d788cea966f1b36"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE:append = " CCOPTS='${CFLAGS}'"
