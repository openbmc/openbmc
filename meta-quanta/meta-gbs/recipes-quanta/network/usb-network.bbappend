FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"
SRC_URI_append_gbs = " file://00-bmc-usb0.network"
SRC_URI_append_gbs = " file://usb-network.conf"

FILES_${PN}_append_gbs = " ${datadir}/usb-network/usb-network.conf"
FILES_${PN}_append_gbs = " ${sysconfdir_native}/systemd/network/00-bmc-usb0.network"

do_install_append_gbs() {
    install -d ${D}${sysconfdir_native}/systemd/network/
    install -m 0644 ${WORKDIR}/00-bmc-usb0.network \
        ${D}${sysconfdir_native}/systemd/network

    install -d ${D}${datadir}/usb-network
    install -m 0644 -D ${WORKDIR}/usb-network.conf \
        ${D}${datadir}/usb-network
}
