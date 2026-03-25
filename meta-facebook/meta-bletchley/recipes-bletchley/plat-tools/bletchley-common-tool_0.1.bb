LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-utils
inherit systemd

S = "${UNPACKDIR}"

RDEPENDS:${PN} += " bash motor-ctrl"
RDEPENDS:${PN} += " mdio-tools"

SRC_URI += " \
    file://bletchley-system-state-init \
    file://bletchley-system-state-init@.service \
    file://bletchley-usbmux-util \
    file://bletchley-net-util \
    "

do_install() {
    install -D -m 0755 ${UNPACKDIR}/bletchley-system-state-init \
        ${D}${libexecdir}/${BPN}/bletchley-system-state-init

    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/bletchley-usbmux-util ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/bletchley-net-util ${D}${bindir}

    install -D -m 0644 ${UNPACKDIR}/bletchley-system-state-init@.service \
        ${D}${systemd_system_unitdir}/bletchley-system-state-init@.service
}

FILES:${PN}:append = " ${systemd_system_unitdir}/bletchley-system-state-init@.service"

SYSTEMD_SERVICE_FMT = "bletchley-system-state-init@{0}.service"
SYSTEMD_SERVICE:${PN} += " ${@compose_list(d, 'SYSTEMD_SERVICE_FMT', 'OBMC_HOST_INSTANCES')}"
