SUMMARY = "Point-to-Point Protocol (PPP) support"
DESCRIPTION = "ppp (Paul's PPP Package) is an open source package which implements \
the Point-to-Point Protocol (PPP) on Linux and Solaris systems."
SECTION = "console/network"
HOMEPAGE = "http://samba.org/ppp/"
BUGTRACKER = "http://ppp.samba.org/cgi-bin/ppp-bugs"
DEPENDS = "libpcap openssl virtual/crypt"
LICENSE = "BSD-3-Clause & BSD-3-Clause-Attribution & GPL-2.0-or-later & LGPL-2.0-or-later & PD"
LIC_FILES_CHKSUM = "file://pppd/ccp.c;beginline=1;endline=29;md5=e2c43fe6e81ff77d87dc9c290a424dea \
                    file://pppd/plugins/passprompt.c;beginline=1;endline=10;md5=3bcbcdbf0e369c9a3e0b8c8275b065d8 \
                    file://pppd/tdb.c;beginline=1;endline=27;md5=4ca3a9991b011038d085d6675ae7c4e6 \
                    file://chat/chat.c;beginline=1;endline=15;md5=0d374b8545ee5c62d7aff1acbd38add2"

SRC_URI = "https://download.samba.org/pub/${BPN}/${BP}.tar.gz \
           file://makefix.patch \
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
           file://0001-ppp-fix-build-against-5.15-headers.patch \
           file://CVE-2022-4603.patch \
           "

SRC_URI[sha256sum] = "f938b35eccde533ea800b15a7445b2f1137da7f88e32a16898d02dee8adc058d"

inherit autotools-brokensep systemd

TARGET_CC_ARCH += " ${LDFLAGS}"
EXTRA_OEMAKE = "CC='${CC}' STRIPPROG=${STRIP} MANDIR=${D}${datadir}/man/man8 INCDIR=${D}${includedir} LIBDIR=${D}${libdir}/pppd/${PV} BINDIR=${D}${sbindir}"
EXTRA_OECONF = "--disable-strip"

# Package Makefile computes CFLAGS, referencing COPTS.
# Typically hard-coded to '-O2 -g' in the Makefile's.
#
EXTRA_OEMAKE += ' COPTS="${CFLAGS} -I${STAGING_INCDIR}/openssl -I${S}/include"'

EXTRA_OECONF:append:libc-musl = " --disable-ipxcp"

do_configure () {
	oe_runconf
}

do_install:append () {
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
	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${WORKDIR}/ppp@.service ${D}${systemd_system_unitdir}
	sed -i -e 's,@SBINDIR@,${sbindir},g' \
	       ${D}${systemd_system_unitdir}/ppp@.service
	rm -rf ${D}/${mandir}/man8/man8
	chmod u+s ${D}${sbindir}/pppd
}

do_install:append:libc-musl () {
	install -Dm 0644 ${S}/include/net/ppp_defs.h ${D}${includedir}/net/ppp_defs.h
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

# Ignore compatibility symlink rp-pppoe.so->pppoe.so
INSANE_SKIP:${PN}-oe += "dev-so"
