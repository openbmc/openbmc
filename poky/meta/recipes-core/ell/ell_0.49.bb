SUMMARY  = "Embedded Linux Library"
HOMEPAGE = "https://01.org/ell"
DESCRIPTION = "The Embedded Linux Library (ELL) provides core, \
low-level functionality for system daemons. It typically has no \
dependencies other than the Linux kernel, C standard library, and \
libdl (for dynamic linking). While ELL is designed to be efficient \
and compact enough for use on embedded Linux platforms, it is not \
limited to resource-constrained systems."
SECTION = "libs"
LICENSE  = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fb504b67c50331fc78734fed90fb0e09"

DEPENDS = "dbus"

inherit autotools pkgconfig

SRC_URI = "https://mirrors.edge.kernel.org/pub/linux/libs/${BPN}/${BPN}-${PV}.tar.xz \
           "
SRC_URI[sha256sum] = "a7ff8ecbc76b187d942dd22b61cb489711400897c790319ffb7e944791687c3f"

do_configure:prepend () {
    mkdir -p ${S}/build-aux
}
