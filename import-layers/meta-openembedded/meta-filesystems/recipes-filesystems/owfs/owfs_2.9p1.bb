SUMMARY = "1-Wire file system"
DESCRIPTION = "OWFS is an easy way to use the powerful 1-wire system of Dallas/Maxim"
HOMEPAGE = "http://www.owfs.org/"
SECTION = "console/network"

LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=a0bc427f423a41220ab79a0b392218bd \
                    file://COPYING.LIB;md5=865c4bd642d9e04f43925ad7e929ae87"

DEPENDS = "fuse virtual/libusb0"

SRC_URI = "${SOURCEFORGE_MIRROR}/owfs/owfs-${PV}.tar.gz \
           file://owhttpd \
           file://owserver "
SRC_URI[md5sum] = "56ba145be208002e58775a7203369851"
SRC_URI[sha256sum] = "9d22dbff72d235476688c02669f7171b23e21dffadf40bbdd3b8263908218424"

inherit autotools-brokensep update-rc.d

EXTRA_OECONF = " \
                 --with-fuseinclude=${STAGING_INCDIR} \
                 --with-fuselib=${STAGING_LIBDIR} \
                 --enable-owfs \
                 --enable-owhttpd \
                 --enable-w1 \
                 --disable-swig \
                 --disable-owtcl \
                 --disable-owphp \
                 --disable-owpython \
                 --disable-owperl \
"

do_install_prepend() {
    install -d ${D}${sysconfdir}/default/
    install -d ${D}${sysconfdir}/init.d/
    install -m 0755 ${WORKDIR}/owhttpd ${D}${sysconfdir}/init.d/owhttpd
    install -m 0755 ${WORKDIR}/owserver ${D}${sysconfdir}/init.d/owserver
}

PACKAGES =+ "owftpd owhttpd owserver owshell libowcapi libow libownet owmon owtap"

DESCRIPTION_owftpd = "Anoymous FTP server for 1-wire access"
DESCRIPTION_owhttpd = "Tiny webserver for 1-wire control"
DESCRIPTION_owserver = "Backend server (daemon) for 1-wire control"
DESCRIPTION_owshell = "owdir owread owwrite owpresent owget - lightweight owserver access"
DESCRIPTION_libowcapi = "easy C-language 1-wire interface "
DESCRIPTION_libow = "easy C-language 1-wire interface to the owserver protocol"
DESCRIPTION_libownet = "easy C-language 1-wire interface to the owserver protocol"
DESCRIPTION_owmon = "Monitor for owserver settings and statistics"
DESCRIPTION_owtap = "Packet sniffer for the owserver protocol"

FILES_owftpd = "${bindir}/owftpd"
FILES_owhttpd = "${bindir}/owhttpd ${sysconfdir}/init.d/owhttpd"
FILES_owserver = "${bindir}/owserver ${sysconfdir}/init.d/owserver"
FILES_owshell = "${bindir}/owread ${bindir}/owwrite \
                 ${bindir}/owdir ${bindir}/owpresent \
                 ${bindir}/owget ${bindir}/owside"
FILES_owmon = "${bindir}/owmon"
FILES_owtap = "${bindir}/owtap"
FILES_libowcapi = "${libdir}/libowcapi-*"
FILES_libow = "${libdir}/libow-*"
FILES_libownet = "${libdir}/libownet-*"

INITSCRIPT_PACKAGES = "owhttpd owserver"
INITSCRIPT_NAME_owserver = "owserver"
INITSCRIPT_NAME_owhttpd = "owhttpd"
INITSCRIPT_PARAMS_owserver = "defaults 20"
INITSCRIPT_PARAMS_owhttpd = "defaults 21"
