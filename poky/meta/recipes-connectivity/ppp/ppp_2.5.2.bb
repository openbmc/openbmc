SUMMARY = "Point-to-Point Protocol (PPP) support"
DESCRIPTION = "ppp (Paul's PPP Package) is an open source package which implements \
the Point-to-Point Protocol (PPP) on Linux and Solaris systems."
SECTION = "console/network"
HOMEPAGE = "http://samba.org/ppp/"
BUGTRACKER = "http://ppp.samba.org/cgi-bin/ppp-bugs"
DEPENDS = "libpcap virtual/crypt"
LICENSE = "BSD-2-Clause & GPL-2.0-or-later & LGPL-2.0-or-later & PD & RSA-MD & MIT"
LIC_FILES_CHKSUM = "file://pppd/ccp.c;beginline=1;endline=25;md5=f0463bd67ae70535c709fca554089bd8 \
                    file://pppd/tdb.c;beginline=1;endline=27;md5=4ca3a9991b011038d085d6675ae7c4e6 \
                    file://chat/chat.c;beginline=1;endline=1;md5=234d7d4edd08962c0144e4604050e0b6 \
                    "

SRC_URI = "https://download.samba.org/pub/${BPN}/${BP}.tar.gz \
           file://pon \
           file://poff \
           file://init \
           file://ip-up \
           file://ip-down \
           file://08setupdns \
           file://92removedns \
           file://pap \
           file://ppp_on_boot \
           file://provider \
           file://ppp@.service \
           "

SRC_URI[sha256sum] = "47da358de54a10cb10bf6ff2cf9b1c03c0d3555518f6182e8f701b8e55733cb2"

inherit autotools pkgconfig systemd

PACKAGECONFIG = "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)} openssl multilink"
PACKAGECONFIG[pam] = "--with-pam=yes,--with-pam=no,libpam"
PACKAGECONFIG[openssl] = "--with-openssl=yes,--with-openssl=no,openssl"
PACKAGECONFIG[multilink] = "--enable-multilink,--disable-multilink"

do_install:append () {
	mkdir -p ${D}${bindir}/ ${D}${sysconfdir}/init.d
	mkdir -p ${D}${sysconfdir}/ppp/ip-up.d/
	mkdir -p ${D}${sysconfdir}/ppp/ip-down.d/
	install -m 0755 ${UNPACKDIR}/pon ${D}${bindir}/pon
	install -m 0755 ${UNPACKDIR}/poff ${D}${bindir}/poff
	install -m 0755 ${UNPACKDIR}/init ${D}${sysconfdir}/init.d/ppp
	install -m 0755 ${UNPACKDIR}/ip-up ${D}${sysconfdir}/ppp/
	install -m 0755 ${UNPACKDIR}/ip-down ${D}${sysconfdir}/ppp/
	install -m 0755 ${UNPACKDIR}/08setupdns ${D}${sysconfdir}/ppp/ip-up.d/
	install -m 0755 ${UNPACKDIR}/92removedns ${D}${sysconfdir}/ppp/ip-down.d/
	mkdir -p ${D}${sysconfdir}/chatscripts
	mkdir -p ${D}${sysconfdir}/ppp/peers
	install -m 0755 ${UNPACKDIR}/pap ${D}${sysconfdir}/chatscripts
	install -m 0755 ${UNPACKDIR}/ppp_on_boot ${D}${sysconfdir}/ppp/ppp_on_boot
	install -m 0755 ${UNPACKDIR}/provider ${D}${sysconfdir}/ppp/peers/provider
	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${UNPACKDIR}/ppp@.service ${D}${systemd_system_unitdir}
	sed -i -e 's,@SBINDIR@,${sbindir},g' \
	       ${D}${systemd_system_unitdir}/ppp@.service
}

CONFFILES:${PN} = "${sysconfdir}/ppp/pap-secrets ${sysconfdir}/ppp/chap-secrets ${sysconfdir}/ppp/options"
PACKAGES =+ "${PN}-oa ${PN}-oe ${PN}-radius ${PN}-winbind ${PN}-minconn ${PN}-password ${PN}-l2tp ${PN}-tools"
FILES:${PN}        = "${sysconfdir} ${bindir} ${sbindir}/chat ${sbindir}/pppd ${systemd_system_unitdir}/ppp@.service"
FILES:${PN}-oa       = "${libdir}/pppd/${PV}/pppoatm.so"
FILES:${PN}-oe       = "${sbindir}/pppoe-discovery ${libdir}/pppd/${PV}/*pppoe.so"
FILES:${PN}-radius   = "${libdir}/pppd/${PV}/radius.so ${libdir}/pppd/${PV}/radattr.so ${libdir}/pppd/${PV}/radrealms.so"
FILES:${PN}-winbind  = "${libdir}/pppd/${PV}/winbind.so"
FILES:${PN}-minconn  = "${libdir}/pppd/${PV}/minconn.so"
FILES:${PN}-password = "${libdir}/pppd/${PV}/pass*.so"
FILES:${PN}-l2tp     = "${libdir}/pppd/${PV}/*l2tp.so"
FILES:${PN}-tools    = "${sbindir}/pppstats ${sbindir}/pppdump"
SUMMARY:${PN}-oa       = "Plugin for PPP for PPP-over-ATM support"
SUMMARY:${PN}-oe       = "Plugin for PPP for PPP-over-Ethernet support"
SUMMARY:${PN}-radius   = "Plugin for PPP for RADIUS support"
SUMMARY:${PN}-winbind  = "Plugin for PPP to authenticate against Samba or Windows"
SUMMARY:${PN}-minconn  = "Plugin for PPP to set a delay before the idle timeout applies"
SUMMARY:${PN}-password = "Plugin for PPP to get passwords via a pipe"
SUMMARY:${PN}-l2tp     = "Plugin for PPP for l2tp support"
SUMMARY:${PN}-tools    = "Additional tools for the PPP package"

