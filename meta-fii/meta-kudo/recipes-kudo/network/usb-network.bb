LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd

DEPENDS += "systemd"
RDEPENDS:${PN} += "libsystemd"
RDEPENDS:${PN} += "bash"

SRC_URI +=" \
    file://usb_network.sh \
    file://usb_network.service \
    file://00-bmc-usb0.network \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} += " usb_network.service"
FILES:${PN} += "${sysconfdir_native}/systemd/network/00-bmc-usb0.network"

do_install() {
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/usb_network.sh ${D}${libexecdir}/${PN}/usb_network.sh

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${UNPACKDIR}/usb_network.service ${D}${systemd_unitdir}/system

    install -d ${D}${sysconfdir_native}/systemd/network/
    install -m 0644 ${UNPACKDIR}/00-bmc-usb0.network ${D}${sysconfdir_native}/systemd/network
}
