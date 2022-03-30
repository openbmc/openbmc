SUMMARY = "Client for Microsoft PPTP VPNs"
DESCRIPTION = "PPTP Client is a Linux, FreeBSD, NetBSD \
    and OpenBSD client for the proprietary Microsoft Point-to-Point \
    Tunneling Protocol, PPTP. Allows connection to a PPTP based \
    Virtual Private Network (VPN) as used by employers and some \
    cable and ADSL internet service providers."
HOMEPAGE = "http://pptpclient.sourceforge.net"
SECTION = "net"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit perlnative

SRC_URI = "${SOURCEFORGE_MIRROR}/sourceforge/pptpclient/pptp-${PV}.tar.gz \
           file://options.pptp \
           "

SRC_URI[md5sum] = "8d25341352fdae5ad5b36b9f18254908"
SRC_URI[sha256sum] = "82492db8e487ce73b182ee7f444251d20c44f5c26d6e96c553ec7093aefb5af4"

S = "${WORKDIR}/pptp-${PV}"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_install() {
    install -d ${D}${sbindir} ${D}${sysconfdir}/ppp ${D}${mandir}/man8
    install -m 555 pptp ${D}${sbindir}
    install -m 644 pptp.8 ${D}${mandir}/man8
    install -m 644 ${WORKDIR}/options.pptp ${D}${sysconfdir}/ppp
}

RDEPENDS:${PN} = "ppp"
