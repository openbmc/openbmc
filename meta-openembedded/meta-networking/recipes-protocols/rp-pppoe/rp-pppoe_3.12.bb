SUMMARY = "A user-mode PPPoE client and server suite for Linux"
HOMEPAGE = "http://www.roaringpenguin.com/products/pppoe"
SECTION = "net"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://doc/LICENSE;md5=a194eaefae2be54ee3221339b10d0581"

PR = "r10"

SRC_URI = "http://www.roaringpenguin.com/files/download/${BP}.tar.gz \
           file://top-autoconf.patch \
           file://configure_in_cross.patch \
           file://pppoe-src-restrictions.patch \
           file://update-config.patch \
           file://dont-swallow-errors.patch \
           file://discard-use-of-dnl-in-Makefile.am.patch \
           file://configure.patch \
           file://pppoe-server.default \
           file://pppoe-server.init \
           file://configure.in-Error-fix.patch \
           file://pppoe-server.service \
           file://0001-ppoe-Dont-include-linux-if_ether.h.patch \
           file://0002-Enable-support-for-the-kernel-module.patch \
           "

SRC_URI[md5sum] = "216eb52b69062b92a64ee37fd71f4b66"
SRC_URI[sha256sum] = "00794e04031546b0e9b8cf286f2a6d1ccfc4a621b2a3abb2d7ef2a7ab7cc86c2"

inherit autotools-brokensep update-rc.d systemd

do_install_append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/pppoe-server.service ${D}${systemd_unitdir}/system
    sed -i -e 's#@SYSCONFDIR@#${sysconfdir}#g' ${D}${systemd_unitdir}/system/pppoe-server.service
    sed -i -e 's#@SBINDIR@#${sbindir}#g' ${D}${systemd_unitdir}/system/pppoe-server.service
    install -d ${D}${datadir}/doc/${PN}
    if [ -f ${D}${datadir}/doc/README ]; then
        mv ${D}${datadir}/doc/README ${D}${datadir}/doc/${PN}/
    fi
}

do_install() {
    # Install init script and default settings
    install -m 0755 -d ${D}${sysconfdir}/default ${D}${sysconfdir}/init.d
    install -m 0644 ${WORKDIR}/pppoe-server.default ${D}${sysconfdir}/default/pppoe-server
    install -m 0755 ${WORKDIR}/pppoe-server.init ${D}${sysconfdir}/init.d/pppoe-server
    # Install
    oe_runmake -C ${S} DESTDIR=${D} docdir=${docdir} install
    chmod 4755 ${D}${sbindir}/pppoe
}

SYSTEMD_PACKAGES = "${PN}-server"
SYSTEMD_SERVICE_${PN}-server = "pppoe-server.service"
SYSTEMD_AUTO_ENABLE = "disable"
# Insert server package before main package
PACKAGES = "${PN}-dbg ${PN}-server ${PN}-relay ${PN}-sniff ${PN} ${PN}-doc"

FILES_${PN}-server = "${sysconfdir}/default/pppoe-server \
                      ${sysconfdir}/init.d/pppoe-server \
                      ${sbindir}/pppoe-server \
                      ${sysconfdir}/ppp/pppoe-server-options"
FILES_${PN}-relay = "${sbindir}/pppoe-relay"
FILES_${PN}-sniff = "${sbindir}/pppoe-sniff"

CONFFILES_${PN} = "${sysconfdir}/ppp/pppoe.conf \
                   ${sysconfdir}/ppp/firewall-standalone \
                   ${sysconfdir}/ppp/firewall-masq"
CONFFILES_${PN}-server = "${sysconfdir}/ppp/pppoe-server-options \
                          ${sysconfdir}/default/pppoe-server"

INITSCRIPT_PACKAGES            = "${PN}-server"
INITSCRIPT_NAME_${PN}-server   = "pppoe-server"
INITSCRIPT_PARAMS_${PN}-server = "defaults 92 8"

RDEPENDS_${PN} = "ppp"
RDEPENDS_${PN}-server = "${PN}"
RRECOMMENDS_${PN} = "ppp-oe"

