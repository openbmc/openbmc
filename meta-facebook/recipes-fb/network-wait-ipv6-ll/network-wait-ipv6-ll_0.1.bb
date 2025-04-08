LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

RDEPENDS:${PN} += " bash"

SRC_URI += " \
    file://check-ipv6-ll \
    file://network-wait-ipv6-ll@.service \
    "

do_install() {
    install -d ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/check-ipv6-ll ${D}${libexecdir}
}

TGT = "${SYSTEMD_DEFAULT_TARGET}"
NCSI_WAIT_IPV6_LL_INSTFMT="../network-wait-ipv6-ll@.service:${TGT}.wants/network-wait-ipv6-ll@{0}.service"

SYSTEMD_SERVICE:${PN} += "network-wait-ipv6-ll@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'NCSI_WAIT_IPV6_LL_INSTFMT', 'FB_ETH_INTF')}"
