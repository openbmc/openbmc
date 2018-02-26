require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://configure-cross.patch \
           file://0001-iproute2-de-bash-scripts.patch \
           file://0001-libc-compat.h-add-musl-workaround.patch \
           file://0001-include-stdint.h-explicitly-for-UINT16_MAX.patch \
           file://0001-ip-Remove-unneed-header.patch \
          "

SRC_URI[md5sum] = "7a9498de88bcca95c305df6108ae197e"
SRC_URI[sha256sum] = "72671028bda696d0cb8f48ec8e702581c3a501caeed33eec3a81d7041cbc8026"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE_append = " CCOPTS='${CFLAGS}'"
