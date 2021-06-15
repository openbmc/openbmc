SUMMARY = "1-Wire file system"
DESCRIPTION = "OWFS is an easy way to use the powerful 1-wire system of Dallas/Maxim"
HOMEPAGE = "http://www.owfs.org/"
SECTION = "console/network"

LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=628b867016631792781a8735a04760e5 \
                    file://COPYING.LIB;md5=9021b7435efdd9fb22beef8291134099"

DEPENDS = "fuse virtual/libusb0"
# v3.2p3
SRCREV = "3744375dfaa350e31c9b360eb1e1a517bbeb5c47"
SRC_URI = "git://github.com/owfs/owfs \
           file://0001-Add-build-rule-for-README.patch \
           file://0001-Fix-compilation-with-GCC10.patch \
           file://owhttpd \
           file://owserver \
           "

S = "${WORKDIR}/git"

inherit autotools-brokensep update-rc.d pkgconfig systemd

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

FILES_owftpd = "${bindir}/owftpd ${systemd_system_unitdir}/owftpd.service"
FILES_owhttpd = "${bindir}/owhttpd ${sysconfdir}/init.d/owhttpd \
                 ${systemd_system_unitdir}/owhttpd.service"
FILES_owserver = "${bindir}/owserver ${sysconfdir}/init.d/owserver \
                  ${systemd_system_unitdir}/owserver.service \
                  ${systemd_system_unitdir}/owserver.socket"
FILES_owshell = "${bindir}/owread ${bindir}/owwrite \
                 ${bindir}/owdir ${bindir}/owpresent \
                 ${bindir}/owget ${bindir}/owside"
FILES_owmon = "${bindir}/owmon"
FILES_owtap = "${bindir}/owtap"
FILES_libowcapi = "${libdir}/libowcapi-*"
FILES_libow = "${libdir}/libow-*"
FILES_libownet = "${libdir}/libownet-*"
FILES_${PN} += "${systemd_system_unitdir}/owfs.service"

INITSCRIPT_PACKAGES = "owhttpd owserver"
INITSCRIPT_NAME_owserver = "owserver"
INITSCRIPT_NAME_owhttpd = "owhttpd"
INITSCRIPT_PARAMS_owserver = "defaults 20"
INITSCRIPT_PARAMS_owhttpd = "defaults 21"

SYSTEMD_SERVICE_${PN} = "owfs.service"
SYSTEMD_SERVICE_${PN}-owftpd = "owftpd.service"
SYSTEMD_SERVICE_${PN}-owhttpd = "owhttpd.service"
SYSTEMD_SERVICE_${PN}-owserver = "owserver.service owserver.socket"
