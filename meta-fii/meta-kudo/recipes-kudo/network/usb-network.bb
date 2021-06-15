LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd

DEPENDS += "systemd"
RDEPENDS_${PN} += "libsystemd"
RDEPENDS_${PN} += "bash"

SRC_URI_append += " \
    file://usb_network.sh \
    file://usb_network.service \
    file://00-bmc-usb0.network \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN}_append = " usb_network.service"
FILES_${PN} += "${sysconfdir_native}/systemd/network/00-bmc-usb0.network"

do_install() {
    install -d ${D}/${sbindir}
    install -m 0755 ${WORKDIR}/usb_network.sh ${D}/${sbindir}

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/usb_network.service ${D}${systemd_unitdir}/system

    install -d ${D}${sysconfdir_native}/systemd/network/
    install -m 0644 ${WORKDIR}/00-bmc-usb0.network ${D}${sysconfdir_native}/systemd/network
}
