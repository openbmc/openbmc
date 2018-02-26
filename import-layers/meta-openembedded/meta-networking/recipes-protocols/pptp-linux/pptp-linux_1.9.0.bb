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

SRC_URI = "${SOURCEFORGE_MIRROR}/sourceforge/pptpclient/pptp-${PV}.tar.gz \
           file://options.pptp \
           file://0001-include-missing-sys-types.h.patch \
           "

SRC_URI[md5sum] = "b2117b377f65294a9786f80f0235d308"
SRC_URI[sha256sum] = "0b1e8cbfc578d3f5ab12ee87c5c2c60419abfe9cc445690a8a19c320b11c9201"

S = "${WORKDIR}/pptp-${PV}"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_install() {
    install -d ${D}${sbindir} ${D}${sysconfdir}/ppp ${D}${mandir}/man8
    install -m 555 pptp ${D}${sbindir}
    install -m 644 pptp.8 ${D}${mandir}/man8
    install -m 644 ${WORKDIR}/options.pptp ${D}${sysconfdir}/ppp
}

RDEPENDS_${PN} = "ppp"
