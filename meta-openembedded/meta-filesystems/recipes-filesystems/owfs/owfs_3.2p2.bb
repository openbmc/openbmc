SUMMARY = "1-Wire file system"
DESCRIPTION = "OWFS is an easy way to use the powerful 1-wire system of Dallas/Maxim"
HOMEPAGE = "http://www.owfs.org/"
SECTION = "console/network"

LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=12a64df1cc87275e940cab05ee75c37d \
                    file://COPYING.LIB;md5=16ff3ffebed582e19ea7a4f48ec77b42"

DEPENDS = "fuse virtual/libusb0"
# v3.2p2
SRCREV = "93c1f36d9ac481075287da331d5184f590f8c0fa"
SRC_URI = "git://github.com/owfs/owfs \
           file://owhttpd \
           file://owserver \
           file://0001-include-sys-sysmacros.h-for-major.patch \
           "

S = "${WORKDIR}/git"

inherit autotools-brokensep update-rc.d pkgconfig

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
