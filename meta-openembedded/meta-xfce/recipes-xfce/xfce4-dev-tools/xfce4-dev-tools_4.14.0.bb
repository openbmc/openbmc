SUMMARY = "Xfce4 development tools"
HOMEPAGE = "http://www.xfce.org"
SECTION = "x11/libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88"
DEPENDS = "glib-2.0"

inherit autotools pkgconfig

BBCLASSEXTEND = "native"

SRC_URI = "http://archive.xfce.org/src/xfce/${BPN}/${@'${PV}'[0:4]}/${BPN}-${PV}.tar.bz2"
SRC_URI[md5sum] = "5f8fc8af73819c08d9e4c26a3ac457e7"
SRC_URI[sha256sum] = "2c9eb8e0fe23e47dc31411a93b683fd1b7a49140e9163f0aab9e94a3d8a0b5fd"

do_install_append() {
    install -d ${D}${datadir}/aclocal
    install -m 644 ${S}/m4macros/*.m4 ${D}${datadir}/aclocal/
}

FILES_${PN} += "${datadir}/xfce4/dev-tools/m4macros/*.m4"

RDEPENDS_${PN} = "bash"
