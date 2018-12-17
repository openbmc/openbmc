SUMMARY = "USB hub per-port power control"
HOMEPAGE = "https://github.com/mvp/uhubctl"
BUGTRACKER = "https://github.com/mvp/uhubctl/issues"
DEPENDS = "libusb1"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "https://github.com/mvp/${PN}/archive/v${PV}.tar.gz"
SRC_URI[md5sum] = "5c711911d766d76813333c3812277574"
SRC_URI[sha256sum] = "4c31278b2c03e5be5a696c3088bc86cf2557a70e00f697799c163aba18e3c40e"

# uhubctl gets its program version from "git describe". As we use the source
# archive do reduce download size replace the call with our hardcoded version.
do_configure_append() {
    sed -i "s/^\(GIT_VERSION :=\).*$/\1 ${PV}/g" ${S}/Makefile
}

do_install () {
    oe_runmake install DESTDIR=${D}
}
