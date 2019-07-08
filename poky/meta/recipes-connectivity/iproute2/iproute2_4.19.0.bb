require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://configure-cross.patch \
           file://0001-libc-compat.h-add-musl-workaround.patch \
           file://0001-ip-Remove-unneed-header.patch \
          "

SRC_URI[md5sum] = "67eeebacaac4515cab73dfd2fc796af3"
SRC_URI[sha256sum] = "d9ec5ca1f47d8a85416fa26e7dc1cbf5d067640eb60e90bdc1c7e5bdc6a29984"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE_append = " CCOPTS='${CFLAGS}'"
