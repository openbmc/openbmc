DESCRIPTION = "A sophisticated console ftp client"
HOMEPAGE = "http://ncftp.com/"
SECTION = "net"
LICENSE = "ClArtistic"
LIC_FILES_CHKSUM = "file://ncftp/cmds.c;beginline=3;endline=4;md5=9de76faeaedc4f908082e3f8142715f4"
DEPENDS = "ncurses"

SRC_URI = "${DEBIAN_MIRROR}/main/n/${BPN}/${BPN}_${PV}.orig.tar.gz \
           file://ncftp-configure-use-BUILD_CC-for-ccdv.patch \
"
SRC_URI[md5sum] = "685e45f60ac11c89442c572c28af4228"
SRC_URI[sha256sum] = "ac111b71112382853b2835c42ebe7bd59acb7f85dd00d44b2c19fbd074a436c4"

inherit autotools-brokensep pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[ccdv] = "--enable-ccdv,--disable-ccdv,,"

do_configure() {
    oe_runconf
}
do_install () {
    install -d ${D}${bindir} ${D}${sysconfdir} ${D}${mandir}
    oe_runmake 'prefix=${D}${prefix}' 'BINDIR=${D}${bindir}' \
        'SYSCONFDIR=${D}${sysconfdir}' 'mandir=${D}${mandir}' \
        install
}
