SUMMARY = "USB hub per-port power control"
HOMEPAGE = "https://github.com/mvp/uhubctl"
BUGTRACKER = "https://github.com/mvp/uhubctl/issues"
DEPENDS = "libusb1"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRCREV = "20276ad5ced147d018e2b3fccedabd94597aa25e"
SRC_URI = "git://github.com/mvp/${BPN};branch=master;protocol=https"
S = "${WORKDIR}/git"

# uhubctl gets its program version from "git describe". As we use the source
# archive do reduce download size replace the call with our hardcoded version.
do_configure:append() {
    sed -i "s/^\(GIT_VERSION :=\).*$/\1 ${PV}/g" ${S}/Makefile
}

do_install () {
    oe_runmake install DESTDIR=${D}
}
