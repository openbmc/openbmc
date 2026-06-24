FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://00-bmc-usb0.network \
    file://90-bmc-usb0-network.rules \
    file://91-bmc-mctpusb-network.rules \
    file://ncsi-bounce \
    file://ncsi-bounce.service \
    "

FILES:${PN}:append = " \
    ${sysconfdir}/systemd/network/00-bmc-usb0.network \
    ${sysconfdir}/udev/rules.d/90-bmc-usb0-network.rules \
    ${sysconfdir}/udev/rules.d/91-bmc-mctpusb-network.rules \
    "

RDEPENDS:${PN}:append = " bash"
SYSTEMD_SERVICE:${PN}:append = " ncsi-bounce.service"

do_install:append() {
    install -d ${D}${sysconfdir}/systemd/network/
    install -m 0644 ${UNPACKDIR}/00-bmc-usb0.network ${D}${sysconfdir}/systemd/network/00-bmc-usb0.network

    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${UNPACKDIR}/90-bmc-usb0-network.rules ${D}${sysconfdir}/udev/rules.d/90-bmc-usb0-network.rules
    install -m 0644 ${UNPACKDIR}/91-bmc-mctpusb-network.rules ${D}${sysconfdir}/udev/rules.d/91-bmc-mctpusb-network.rules

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/ncsi-bounce ${D}${libexecdir}/${PN}/
    install -m 0644 ${UNPACKDIR}/ncsi-bounce.service ${D}${systemd_system_unitdir}
}
