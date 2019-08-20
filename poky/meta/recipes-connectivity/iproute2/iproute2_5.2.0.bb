require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://0001-libc-compat.h-add-musl-workaround.patch \
          "

SRC_URI[md5sum] = "0cb2736e7bc2f56254a363d3d23703b7"
SRC_URI[sha256sum] = "a5b95dec26353fc71dba9bb403e9343fad2a06bd69fb154a22a2aa2914f74da8"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE_append = " CCOPTS='${CFLAGS}'"
