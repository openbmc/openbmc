FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://00-bmc-usb0.network \
    "

FILES:${PN}:append = " ${systemd_unitdir}/network/00-bmc-usb0.network"

do_install:append() {
    install -d ${D}${systemd_unitdir}/network/
    install -m 0644 ${UNPACKDIR}/00-bmc-usb0.network ${D}${systemd_unitdir}/network/00-bmc-usb0.network
}
