DESCRIPTION = "A sophisticated console ftp client"
HOMEPAGE = "http://ncftp.com/"
SECTION = "net"
LICENSE = "ClArtistic"
LIC_FILES_CHKSUM = "file://ncftp/cmds.c;beginline=3;endline=4;md5=9c2390809f71465aa7ff76e03dc14d91"
DEPENDS = "ncurses"

SRC_URI = "ftp://ftp.ncftp.com/${BPN}/${BP}-src.tar.xz \
           file://ncftp-configure-use-BUILD_CC-for-ccdv.patch \
           file://unistd.patch \
"
SRC_URI[md5sum] = "42d0f896d69a4d603ec097546444245f"
SRC_URI[sha256sum] = "5f200687c05d0807690d9fb770327b226f02dd86155b49e750853fce4e31098d"

inherit autotools-brokensep pkgconfig

CFLAGS += "-DNO_SSLv2 -D_FILE_OFFSET_BITS=64 -Wall"

PACKAGECONFIG ??= ""
PACKAGECONFIG[ccdv] = "--enable-ccdv,--disable-ccdv,,"

EXTRA_OECONF = "--disable-precomp"
TARGET_CC_ARCH_append = " ${SELECTED_OPTIMIZATION}"

do_configure() {
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}
    oe_runconf
}
do_install () {
    install -d ${D}${bindir} ${D}${sysconfdir} ${D}${mandir}
    oe_runmake 'prefix=${D}${prefix}' 'BINDIR=${D}${bindir}' \
        'SYSCONFDIR=${D}${sysconfdir}' 'mandir=${D}${mandir}' \
        install
}
