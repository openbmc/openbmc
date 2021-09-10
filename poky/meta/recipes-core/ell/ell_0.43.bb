SUMMARY  = "Embedded Linux Library"
HOMEPAGE = "https://01.org/ell"
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

SRC_URI = "https://mirrors.edge.kernel.org/pub/linux/libs/${BPN}/${BPN}-${PV}.tar.xz \
           file://0001-pem.c-do-not-use-rawmemchr.patch \
           "
SRC_URI[sha256sum] = "b6c75895ad64bf78f5b5c464de871048d02f9403be518084e0d64b58f508c9fc"

do_configure:prepend () {
    mkdir -p ${S}/build-aux
}
