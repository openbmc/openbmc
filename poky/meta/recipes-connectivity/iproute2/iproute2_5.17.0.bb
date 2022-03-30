require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://0001-libc-compat.h-add-musl-workaround.patch \
           "

SRC_URI[sha256sum] = "6e384f1b42c75e1a9daac57866da37dcff909090ba86eb25a6e764da7893660e"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE:append = " CCOPTS='${CFLAGS}'"
