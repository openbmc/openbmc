SUMMARY = "A user-mode PPPoE client and server suite for Linux"
HOMEPAGE = "http://www.roaringpenguin.com/products/pppoe"
SECTION = "net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://doc/LICENSE;md5=a194eaefae2be54ee3221339b10d0581"

PR = "r11"

SRC_URI = "https://dianne.skoll.ca/projects/rp-pppoe/download/OLD/rp-pppoe-${PV}.tar.gz \
           file://top-autoconf.patch \
           file://configure_in_cross.patch \
           file://update-config.patch \
           file://discard-use-of-dnl-in-Makefile.am.patch \
           file://configure.patch \
           file://pppoe-server.default \
           file://pppoe-server.init \
           file://pppoe-server.service \
           file://0001-ppoe-Dont-include-linux-if_ether.h.patch \
           file://0002-Enable-support-for-the-kernel-module.patch \
           "

SRC_URI[sha256sum] = "b1f318bc7e4e5b0fd8a8e23e8803f5e6e43165245a5a10a7162a92a6cf17829a"

inherit autotools-brokensep update-rc.d systemd

CACHED_CONFIGUREVARS += "${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'be', 'rpppoe_cv_pack_bitfields=normal', 'rpppoe_cv_pack_bitfields=rev', d)}"

# Needed for strlcpy()
CFLAGS += "-D_GNU_SOURCE"

do_install:append() {
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
SYSTEMD_SERVICE:${PN}-server = "pppoe-server.service"
SYSTEMD_AUTO_ENABLE = "disable"
# Insert server package before main package
PACKAGES = "${PN}-dbg ${PN}-server ${PN}-relay ${PN}-sniff ${PN} ${PN}-doc"

FILES:${PN}-server = "${sysconfdir}/default/pppoe-server \
                      ${sysconfdir}/init.d/pppoe-server \
                      ${sbindir}/pppoe-server \
                      ${sysconfdir}/ppp/pppoe-server-options"
FILES:${PN}-relay = "${sbindir}/pppoe-relay"
FILES:${PN}-sniff = "${sbindir}/pppoe-sniff"

CONFFILES:${PN} = "${sysconfdir}/ppp/pppoe.conf \
                   ${sysconfdir}/ppp/firewall-standalone \
                   ${sysconfdir}/ppp/firewall-masq"
CONFFILES:${PN}-server = "${sysconfdir}/ppp/pppoe-server-options \
                          ${sysconfdir}/default/pppoe-server"

INITSCRIPT_PACKAGES            = "${PN}-server"
INITSCRIPT_NAME:${PN}-server   = "pppoe-server"
INITSCRIPT_PARAMS:${PN}-server = "defaults 92 8"

RDEPENDS:${PN} = "ppp"
RDEPENDS:${PN}-server = "${PN}"
RRECOMMENDS:${PN} = "ppp-oe"

