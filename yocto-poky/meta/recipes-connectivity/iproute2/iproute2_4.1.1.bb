require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://configure-cross.patch \
           file://0001-iproute2-de-bash-scripts.patch \
          "
SRC_URI[md5sum] = "39290cb3a55d38dd8d10e19a3094109f"
SRC_URI[sha256sum] = "73077a989efb934450bd655cbd9aaddaa747cb696c64d0c9a3323768a6a8e66f"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE_append = " CCOPTS='${CFLAGS}'"
