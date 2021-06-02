require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://0001-libc-compat.h-add-musl-workaround.patch \
           "

SRC_URI[sha256sum] = "9d268db98a36ee2a0e3ff3b92b2efff66fc1138a51e409bdef6ab3cfe15f326f"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE_append = " CCOPTS='${CFLAGS}'"
