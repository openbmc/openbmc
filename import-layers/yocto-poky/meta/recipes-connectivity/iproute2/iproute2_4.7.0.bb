require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://configure-cross.patch \
           file://0001-iproute2-de-bash-scripts.patch \
           file://iproute2-4.3.0-musl.patch \
          "
SRC_URI[md5sum] = "d4b205830cdc2702f8a0cbd6232129cd"
SRC_URI[sha256sum] = "8f60dbcfb33a79daae0638f53bdcaa4310c0aa59ae39af8a234020dc69bb7b92"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE_append = " CCOPTS='${CFLAGS}'"
