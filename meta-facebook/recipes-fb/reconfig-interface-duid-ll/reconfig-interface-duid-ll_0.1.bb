LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-utils
inherit systemd

S = "${UNPACKDIR}"

RDEPENDS:${PN} += " bash"

SRC_URI += " \
    file://reconfig-interface-duid-ll \
    file://reconfig-interface-duid-ll@.path \
    file://reconfig-interface-duid-ll@.service \
    "

do_install() {
    install -d ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/reconfig-interface-duid-ll ${D}${libexecdir}

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/reconfig-interface-duid-ll@.path ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/reconfig-interface-duid-ll@.service ${D}${systemd_system_unitdir}
}

FILES:${PN}:append = " \
    ${systemd_system_unitdir}/reconfig-interface-duid-ll@.path \
    ${systemd_system_unitdir}/reconfig-interface-duid-ll@.service \
    "

SYSTEMD_SERVICE_FMT = "reconfig-interface-duid-ll@{0}.path reconfig-interface-duid-ll@{0}.service"
SYSTEMD_SERVICE = "${@compose_list(d, 'SYSTEMD_SERVICE_FMT', 'FB_ETH_INTF')}"
