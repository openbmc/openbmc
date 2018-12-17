SUMMARY = "Point-to-Point Protocol (PPP) support"
DESCRIPTION = "ppp (Paul's PPP Package) is an open source package which implements \
the Point-to-Point Protocol (PPP) on Linux and Solaris systems."
SECTION = "console/network"
HOMEPAGE = "http://samba.org/ppp/"
BUGTRACKER = "http://ppp.samba.org/cgi-bin/ppp-bugs"
DEPENDS = "libpcap openssl virtual/crypt"
LICENSE = "BSD & GPLv2+ & LGPLv2+ & PD"
LIC_FILES_CHKSUM = "file://pppd/ccp.c;beginline=1;endline=29;md5=e2c43fe6e81ff77d87dc9c290a424dea \
                    file://pppd/plugins/passprompt.c;beginline=1;endline=10;md5=3bcbcdbf0e369c9a3e0b8c8275b065d8 \
                    file://pppd/tdb.c;beginline=1;endline=27;md5=4ca3a9991b011038d085d6675ae7c4e6 \
                    file://chat/chat.c;beginline=1;endline=15;md5=0d374b8545ee5c62d7aff1acbd38add2"

SRC_URI = "https://download.samba.org/pub/${BPN}/${BP}.tar.gz \
           file://makefile.patch \
           file://cifdefroute.patch \
           file://pppd-resolv-varrun.patch \
           file://makefile-remove-hard-usr-reference.patch \
           file://pon \
           file://poff \
           file://init \
           file://ip-up \
           file://ip-down \
           file://08setupdns \
           file://92removedns \
           file://copts.patch \
           file://pap \
           file://ppp_on_boot \
           file://provider \
           file://0001-ppp-Fix-compilation-errors-in-Makefile.patch \
           file://ppp@.service \
           file://fix-CVE-2015-3310.patch \
           file://0001-pppoe-include-netinet-in.h-before-linux-in.h.patch \
           file://0001-ppp-Remove-unneeded-include.patch \
           file://ppp-2.4.7-DES-openssl.patch \
"

SRC_URI_append_libc-musl = "\
           file://0001-Fix-build-with-musl.patch \
"
SRC_URI[md5sum] = "78818f40e6d33a1d1de68a1551f6595a"
SRC_URI[sha256sum] = "02e0a3dd3e4799e33103f70ec7df75348c8540966ee7c948e4ed8a42bbccfb30"

inherit autotools-brokensep systemd

TARGET_CC_ARCH += " ${LDFLAGS}"
EXTRA_OEMAKE = "STRIPPROG=${STRIP} MANDIR=${D}${datadir}/man/man8 INCDIR=${D}${includedir} LIBDIR=${D}${libdir}/pppd/${PV} BINDIR=${D}${sbindir}"
EXTRA_OECONF = "--disable-strip"

# Package Makefile computes CFLAGS, referencing COPTS.
# Typically hard-coded to '-O2 -g' in the Makefile's.
#
EXTRA_OEMAKE += ' COPTS="${CFLAGS} -I${STAGING_INCDIR}/openssl -I${S}/include"'

do_configure () {
	oe_runconf
}

do_install_append () {
	make install-etcppp ETCDIR=${D}/${sysconfdir}/ppp
	mkdir -p ${D}${bindir}/ ${D}${sysconfdir}/init.d
	mkdir -p ${D}${sysconfdir}/ppp/ip-up.d/
	mkdir -p ${D}${sysconfdir}/ppp/ip-down.d/
	install -m 0755 ${WORKDIR}/pon ${D}${bindir}/pon
	install -m 0755 ${WORKDIR}/poff ${D}${bindir}/poff
	install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/ppp
	install -m 0755 ${WORKDIR}/ip-up ${D}${sysconfdir}/ppp/
	install -m 0755 ${WORKDIR}/ip-down ${D}${sysconfdir}/ppp/
	install -m 0755 ${WORKDIR}/08setupdns ${D}${sysconfdir}/ppp/ip-up.d/
	install -m 0755 ${WORKDIR}/92removedns ${D}${sysconfdir}/ppp/ip-down.d/
	mkdir -p ${D}${sysconfdir}/chatscripts
	mkdir -p ${D}${sysconfdir}/ppp/peers
	install -m 0755 ${WORKDIR}/pap ${D}${sysconfdir}/chatscripts
	install -m 0755 ${WORKDIR}/ppp_on_boot ${D}${sysconfdir}/ppp/ppp_on_boot
	install -m 0755 ${WORKDIR}/provider ${D}${sysconfdir}/ppp/peers/provider
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/ppp@.service ${D}${systemd_unitdir}/system
	sed -i -e 's,@SBINDIR@,${sbindir},g' \
	       ${D}${systemd_unitdir}/system/ppp@.service
	rm -rf ${D}/${mandir}/man8/man8
	chmod u+s ${D}${sbindir}/pppd
}

do_install_append_libc-musl () {
	install -Dm 0644 ${S}/include/net/ppp_defs.h ${D}${includedir}/net/ppp_defs.h
}

CONFFILES_${PN} = "${sysconfdir}/ppp/pap-secrets ${sysconfdir}/ppp/chap-secrets ${sysconfdir}/ppp/options"
PACKAGES =+ "${PN}-oa ${PN}-oe ${PN}-radius ${PN}-winbind ${PN}-minconn ${PN}-password ${PN}-l2tp ${PN}-tools"
FILES_${PN}        = "${sysconfdir} ${bindir} ${sbindir}/chat ${sbindir}/pppd ${systemd_unitdir}/system/ppp@.service"
FILES_${PN}-oa       = "${libdir}/pppd/${PV}/pppoatm.so"
FILES_${PN}-oe       = "${sbindir}/pppoe-discovery ${libdir}/pppd/${PV}/rp-pppoe.so"
FILES_${PN}-radius   = "${libdir}/pppd/${PV}/radius.so ${libdir}/pppd/${PV}/radattr.so ${libdir}/pppd/${PV}/radrealms.so"
FILES_${PN}-winbind  = "${libdir}/pppd/${PV}/winbind.so"
FILES_${PN}-minconn  = "${libdir}/pppd/${PV}/minconn.so"
FILES_${PN}-password = "${libdir}/pppd/${PV}/pass*.so"
FILES_${PN}-l2tp     = "${libdir}/pppd/${PV}/*l2tp.so"
FILES_${PN}-tools    = "${sbindir}/pppstats ${sbindir}/pppdump"
SUMMARY_${PN}-oa       = "Plugin for PPP for PPP-over-ATM support"
SUMMARY_${PN}-oe       = "Plugin for PPP for PPP-over-Ethernet support"
SUMMARY_${PN}-radius   = "Plugin for PPP for RADIUS support"
SUMMARY_${PN}-winbind  = "Plugin for PPP to authenticate against Samba or Windows"
SUMMARY_${PN}-minconn  = "Plugin for PPP to set a delay before the idle timeout applies"
SUMMARY_${PN}-password = "Plugin for PPP to get passwords via a pipe"
SUMMARY_${PN}-l2tp     = "Plugin for PPP for l2tp support"
SUMMARY_${PN}-tools    = "Additional tools for the PPP package"
