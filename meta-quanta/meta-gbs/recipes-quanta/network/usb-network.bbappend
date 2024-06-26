FILESEXTRAPATHS:prepend:gbs := "${THISDIR}/${PN}:"
SRC_URI:append:gbs = " file://00-bmc-usb0.network"
SRC_URI:append:gbs = " file://usb-network.conf"

FILES:${PN}:append:gbs = " ${datadir}/usb-network/usb-network.conf"
FILES:${PN}:append:gbs = " ${sysconfdir_native}/systemd/network/00-bmc-usb0.network"

do_install:append:gbs() {
    install -d ${D}${sysconfdir_native}/systemd/network/
    install -m 0644 ${UNPACKDIR}/00-bmc-usb0.network \
        ${D}${sysconfdir_native}/systemd/network

    install -d ${D}${datadir}/usb-network
    install -m 0644 -D ${UNPACKDIR}/usb-network.conf \
        ${D}${datadir}/usb-network
}
