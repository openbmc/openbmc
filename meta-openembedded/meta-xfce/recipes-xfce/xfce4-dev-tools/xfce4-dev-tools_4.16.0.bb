SUMMARY = "Xfce4 development tools"
HOMEPAGE = "http://www.xfce.org"
SECTION = "x11/libs"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88"

DEPENDS = "glib-2.0"
DEPENDS:append:class-target = " ${BPN}-native"

inherit autotools pkgconfig

BBCLASSEXTEND = "native"

SRC_URI = "http://archive.xfce.org/src/xfce/${BPN}/${@'${PV}'[0:4]}/${BPN}-${PV}.tar.bz2"
SRC_URI:append:class-target = " file://0001-Run-native-xdt-csource-on-tests.patch"
SRC_URI[sha256sum] = "f50b3070e66f3ebdf331744dd1ec5e1af5de333965d491e15ce05545e8eb4f04"

do_install:append() {
    install -d ${D}${datadir}/aclocal
    install -m 644 ${S}/m4macros/*.m4 ${D}${datadir}/aclocal/
}

FILES:${PN} += "${datadir}/xfce4/dev-tools/m4macros/*.m4"

RDEPENDS:${PN} = "bash"
