require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://configure-cross.patch \
           file://0001-libc-compat.h-add-musl-workaround.patch \
           file://0001-ip-Remove-unneed-header.patch \
          "

SRC_URI[md5sum] = "8b8680e91390c57cab788fbf8e929479"
SRC_URI[sha256sum] = "a9e6c70c95f513871c5e1f4e452c04fcb3c4d8a05be651bd794cd994a52daa45"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE_append = " CCOPTS='${CFLAGS}'"
