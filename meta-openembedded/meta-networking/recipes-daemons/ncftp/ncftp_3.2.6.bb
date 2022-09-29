DESCRIPTION = "A sophisticated console ftp client"
HOMEPAGE = "http://ncftp.com/"
SECTION = "net"
LICENSE = "ClArtistic"
LIC_FILES_CHKSUM = "file://ncftp/cmds.c;beginline=3;endline=4;md5=9c2390809f71465aa7ff76e03dc14d91"
DEPENDS = "ncurses"

SRC_URI = "ftp://ftp.ncftp.com/${BPN}/${BP}-src.tar.xz \
           file://ncftp-configure-use-BUILD_CC-for-ccdv.patch \
           file://unistd.patch \
           file://ncftp-3.2.5-gcc10.patch \
           file://0001-Forward-port-defining-PREFIX_BINDIR-to-use-new-autoc.patch \
           "
SRC_URI[md5sum] = "42d0f896d69a4d603ec097546444245f"
SRC_URI[sha256sum] = "5f200687c05d0807690d9fb770327b226f02dd86155b49e750853fce4e31098d"

inherit autotools-brokensep pkgconfig

CFLAGS += "-DNO_SSLv2 -D_FILE_OFFSET_BITS=64 -Wall"

PACKAGECONFIG ??= ""
PACKAGECONFIG[ccdv] = "--enable-ccdv,--disable-ccdv,,"

EXTRA_OECONF = "--disable-precomp --disable-universal ac_cv_path_TAR=tar"
ACLOCALEXTRAPATH:append = " -I ${S}/autoconf_local"

do_install () {
    install -d ${D}${bindir} ${D}${sysconfdir} ${D}${mandir}
    oe_runmake 'prefix=${D}${prefix}' 'BINDIR=${D}${bindir}' \
        'SYSCONFDIR=${D}${sysconfdir}' 'mandir=${D}${mandir}' \
        install
}
