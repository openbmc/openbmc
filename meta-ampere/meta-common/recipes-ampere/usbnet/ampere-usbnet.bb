SUMMARY = "Ampere Computing LLC Add Ethernet over USB gadget device"
DESCRIPTION = "Add Ethernet over USB gadget device for Ampere systems"
PR = "r1"

LICENSE = "Apache-2.0"
S = "${WORKDIR}"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

DEPENDS = "systemd"
RDEPENDS_${PN} = "bash"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = " \
        ampere_add_usbnet_gadget.service \
        "

SRC_URI += "file://00-bmc-usb0.network"
SRC_URI += "file://ampere_add_usbnet_gadget.sh"

do_install_append() {
        install -d ${D}${sbindir}
        install -d ${D}/etc/systemd/network
        install -m 744 ${WORKDIR}/ampere_add_usbnet_gadget.sh ${D}${sbindir}/
        install -m 644 ${WORKDIR}/00-bmc-usb0.network \
                       ${D}/etc/systemd/network/
}
