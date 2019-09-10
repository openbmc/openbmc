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
SRC_URI[md5sum] = "a4e7d74404f11e71775b89f53a8f1c33"
SRC_URI[sha256sum] = "3c1d6d997e17dfcbe4ebcd1331d9a7be5c64f2f0a0813bc223790e570d8da2e3"

do_configure_prepend () {
    mkdir -p ${S}/build-aux
}
