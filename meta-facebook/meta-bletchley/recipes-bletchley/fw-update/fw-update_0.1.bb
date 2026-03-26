SUMMARY = "Firmware update tools"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-utils
inherit systemd

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "libgpiod-tools"

S = "${UNPACKDIR}"
SRC_URI += " \
    file://usb-controller-update \
    file://flash-usb-controller@.service \
"

SYSTEMD_SERVICE_FMT = "flash-usb-controller@{0}.service"
SYSTEMD_SERVICE:${PN} = "${@compose_list(d, 'SYSTEMD_SERVICE_FMT', 'OBMC_HOST_INSTANCES')}"

FILES:${PN} += "${systemd_system_unitdir}/flash-usb-controller@.service"

do_install() {
        install -d ${D}${sbindir}
        install -m 0755 ${UNPACKDIR}/usb-controller-update ${D}${sbindir}

        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/flash-usb-controller@.service ${D}${systemd_system_unitdir}
}
