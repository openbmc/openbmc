require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://0001-libc-compat.h-add-musl-workaround.patch \
          "

SRC_URI[md5sum] = "ee8e2cdb416d4a8ef39525d39ab7c2d0"
SRC_URI[sha256sum] = "bac543435cac208a11db44c9cc8e35aa902befef8750594654ee71941c388f7b"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE_append = " CCOPTS='${CFLAGS}'"
