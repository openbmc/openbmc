require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://0001-libc-compat.h-add-musl-workaround.patch \
           file://0001-devlink.c-add-missing-include.patch \
           "

SRC_URI[sha256sum] = "cfcd1f890290f8c8afcc91d9444ad929b9252c16f9ab3f286c50dd3c59dc646e"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE_append = " CCOPTS='${CFLAGS}'"
