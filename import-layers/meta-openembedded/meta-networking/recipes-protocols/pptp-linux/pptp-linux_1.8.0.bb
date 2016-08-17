SUMMARY = "Client for Microsoft PPTP VPNs"
DESCRIPTION = "PPTP Client is a Linux, FreeBSD, NetBSD \
    and OpenBSD client for the proprietary Microsoft Point-to-Point \
    Tunneling Protocol, PPTP. Allows connection to a PPTP based \
    Virtual Private Network (VPN) as used by employers and some \
    cable and ADSL internet service providers."
HOMEPAGE = "http://pptpclient.sourceforge.net"
SECTION = "net"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

PR = "r1"

SRC_URI = "${SOURCEFORGE_MIRROR}/sourceforge/pptpclient/pptp-${PV}.tar.gz \
           file://options.pptp \
"

SRC_URI[md5sum] = "4efce9f263e2c3f38d79d9df222476de"
SRC_URI[sha256sum] = "e39c42d933242a8a6dd8600a0fa7f0a5ec8f066d10c4149d8e81a5c68fe4bbda"

S = "${WORKDIR}/pptp-${PV}"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_install() {
    install -d ${D}${sbindir} ${D}${sysconfdir}/ppp ${D}${mandir}/man8
    install -m 555 pptp ${D}${sbindir}
    install -m 644 pptp.8 ${D}${mandir}/man8
    install -m 644 ${WORKDIR}/options.pptp ${D}${sysconfdir}/ppp
}

RDEPENDS_${PN} = "ppp"
