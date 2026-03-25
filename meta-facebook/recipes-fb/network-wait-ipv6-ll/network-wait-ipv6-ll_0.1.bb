LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-utils
inherit systemd

S = "${UNPACKDIR}"

RDEPENDS:${PN} += " bash"

SRC_URI += " \
    file://check-ipv6-ll \
    file://network-wait-ipv6-ll@.service \
    "

do_install() {
    install -D -m 0755 ${UNPACKDIR}/check-ipv6-ll ${D}${libexecdir}/${BPN}/check-ipv6-ll
    install -D -m 0644 ${UNPACKDIR}/network-wait-ipv6-ll@.service ${D}${systemd_system_unitdir}/network-wait-ipv6-ll@.service
}

FILES:${PN}:append = " ${systemd_system_unitdir}/network-wait-ipv6-ll@.service"

SYSTEMD_SERVICE_FMT = "network-wait-ipv6-ll@{0}.service"
SYSTEMD_SERVICE:${PN}:append = " ${@compose_list(d, 'SYSTEMD_SERVICE_FMT', 'FB_ETH_INTF')}"
