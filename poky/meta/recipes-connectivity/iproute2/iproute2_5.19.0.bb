require iproute2.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://0001-libc-compat.h-add-musl-workaround.patch \
           file://0001-ip-ipstats.c-add-an-include-where-MIN-is-defined.patch \
           file://0001-configure-Define-_GNU_SOURCE-when-checking-for-setns.patch \
           "

SRC_URI[sha256sum] = "26b7a34d6a7fd2f7a42e2b39c5a90cb61bac522d1096067ffeb195e5693d7791"

# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE:append = " CCOPTS='${CFLAGS}'"
