require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://configure-cross.patch \
           file://0001-iproute2-de-bash-scripts.patch \
           file://iproute2-4.3.0-musl.patch \
           file://iproute2-fix-building-with-musl.patch \
          "
SRC_URI[md5sum] = "d762653ec3e1ab0d4a9689e169ca184f"
SRC_URI[sha256sum] = "bc91c367288a19f78ef800cd6840363be1f22da8436fbae88e1a7250490d6514"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE_append = " CCOPTS='${CFLAGS}'"
