FILESEXTRAPATHS_prepend := "${THISDIR}/network:"
SRC_URI_append_ibm-ac-server = " file://ncsi-netlink.service"
SYSTEMD_SERVICE_${PN}_append_ibm-ac-server = " ncsi-netlink.service"

do_install_append_ibm-ac-server() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/ncsi-netlink.service ${D}${systemd_system_unitdir}
}
