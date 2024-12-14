SUMMARY = "USB hub per-port power control"
HOMEPAGE = "https://github.com/mvp/uhubctl"
BUGTRACKER = "https://github.com/mvp/uhubctl/issues"
DEPENDS = "libusb1"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRCREV = "352f5878e999c0a9d5a453b34110479b2056d7e7"
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
