SUMMARY = "Firmware update tools"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "libgpiod-tools"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"
SRC_URI += " \
    file://usb-controller-update \
"

do_install() {
        install -d ${D}${sbindir}
        install -m 0755 ${UNPACKDIR}/usb-controller-update ${D}${sbindir}
}

FLASH_USB_CONTROLLER_INSTFMT="flash-usb-controller@.service:flash-usb-controller@{0}.service"
SYSTEMD_SERVICE:${PN} += "flash-usb-controller@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'FLASH_USB_CONTROLLER_INSTFMT', 'OBMC_HOST_INSTANCES')}"
