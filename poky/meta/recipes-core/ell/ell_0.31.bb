SUMMARY  = "Embedded Linux Library"
DESCRIPTION = "The Embedded Linux Library (ELL) provides core, \
low-level functionality for system daemons. It typically has no \
dependencies other than the Linux kernel, C standard library, and \
libdl (for dynamic linking). While ELL is designed to be efficient \
and compact enough for use on embedded Linux platforms, it is not \
limited to resource-constrained systems."
SECTION = "libs"
LICENSE  = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fb504b67c50331fc78734fed90fb0e09"

DEPENDS = "dbus"

inherit autotools pkgconfig

SRC_URI = "https://mirrors.edge.kernel.org/pub/linux/libs/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "3f670230be4d89d621b0508c70b1d36b"
SRC_URI[sha256sum] = "ae88617275452f9f5840b2365e33e6c7fb6fa3405d42cbf9367de642ee8b6701"

do_configure_prepend () {
    mkdir -p ${S}/build-aux
}
