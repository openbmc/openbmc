SUMMARY = "Xfce4 development tools"
HOMEPAGE = "http://www.xfce.org"
SECTION = "x11/libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88"
DEPENDS = "glib-2.0"

inherit autotools pkgconfig

BBCLASSEXTEND = "native"

SRC_URI = "http://archive.xfce.org/src/xfce/${BPN}/${@'${PV}'[0:4]}/${BPN}-${PV}.tar.bz2"
SRC_URI[md5sum] = "559202c4d9650e23696c44aa94cfc5a9"
SRC_URI[sha256sum] = "e2e3a654fe9110df81f8c2483c9cbfa6d656fed15d5e5e717d6ef10bd0f5b5cb"

do_install_append() {
    install -d ${D}${datadir}/aclocal
    install -m 644 ${S}/m4macros/*.m4 ${D}${datadir}/aclocal/
}

FILES_${PN} += "${datadir}/xfce4/dev-tools/m4macros/*.m4"

RDEPENDS_${PN} = "bash"
