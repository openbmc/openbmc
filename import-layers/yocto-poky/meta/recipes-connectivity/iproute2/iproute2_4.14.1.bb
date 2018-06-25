require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://configure-cross.patch \
           file://0001-iproute2-de-bash-scripts.patch \
           file://0001-libc-compat.h-add-musl-workaround.patch \
           file://0001-ip-Remove-unneed-header.patch \
          "

SRC_URI[md5sum] = "1075423d7029e02a8f23ed4f42b7e372"
SRC_URI[sha256sum] = "d43ac068afcc350a448f4581b6e292331ef7e4e7aa746e34981582d5fdb10067"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE_append = " CCOPTS='${CFLAGS}'"
