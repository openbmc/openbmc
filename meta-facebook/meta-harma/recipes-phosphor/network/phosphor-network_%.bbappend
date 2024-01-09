FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:harma = " \
    file://00-bmc-eth0.network \
    file://00-bmc-usb0.network \
    "

FILES:${PN}:append:harma = " ${systemd_unitdir}/network/00-bmc-eth0.network"
FILES:${PN}:append:harma = " ${systemd_unitdir}/network/00-bmc-usb0.network"

do_install:append() {
    install -d ${D}${systemd_unitdir}/network/
    install -m 0644 ${WORKDIR}/00-bmc-eth0.network ${D}${systemd_unitdir}/network/00-bmc-eth0.network
    install -m 0644 ${WORKDIR}/00-bmc-usb0.network ${D}${systemd_unitdir}/network/00-bmc-usb0.network
}
