require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://0001-libc-compat.h-add-musl-workaround.patch \
          "

SRC_URI[md5sum] = "227404413c8d6db649d6188ead1e5a6e"
SRC_URI[sha256sum] = "cb1c1e45993a3bd2438543fd4332d70f1726a6e6ff97dc613a8258c993117b3f"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE_append = " CCOPTS='${CFLAGS}'"
