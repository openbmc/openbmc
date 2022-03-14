require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://0001-libc-compat.h-add-musl-workaround.patch \
           file://0001-lib-fix-ax25.h-include-for-musl.patch \
           "

SRC_URI[sha256sum] = "c064b66f6b001c2a35aa5224b5b1ac8aa4bee104d7dce30d6f10a84cb8b01e2f"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE:append = " CCOPTS='${CFLAGS}'"
