SUMMARY = "Xfce4 development tools"
HOMEPAGE = "http://www.xfce.org"
SECTION = "x11/libs"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "glib-2.0"
DEPENDS:append:class-target = " ${BPN}-native"

SRC_URI = "http://archive.xfce.org/src/xfce/${BPN}/${@'${PV}'[0:4]}/${BPN}-${PV}.tar.bz2 \
           file://0001-m4macros-Check-for-a-function-provided-by-libX11-in-.patch \
           "
SRC_URI:append:class-target = " file://0001-Run-native-xdt-csource-on-tests.patch"
SRC_URI[sha256sum] = "eedb4fc955f0e3459c46864ff98579295db2b900743e0ff69cad5970ba76be37"

inherit autotools pkgconfig

do_install:append() {
    install -d ${D}${datadir}/aclocal
    install -m 644 ${S}/m4macros/*.m4 ${D}${datadir}/aclocal/
}

FILES:${PN} += "${datadir}/xfce4/dev-tools/m4macros/*.m4"

RDEPENDS:${PN} = "bash"

BBCLASSEXTEND = "native"
