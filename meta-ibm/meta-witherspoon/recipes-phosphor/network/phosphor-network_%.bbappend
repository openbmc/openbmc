FILESEXTRAPATHS_prepend := "${THISDIR}/network:"
SRC_URI_append_ibm-ac-server = " file://ncsi-netlink.service"
SRC_URI_append_mihawk = " file://ncsi-netlink.service"

SYSTEMD_SERVICE_${PN}_append_ibm-ac-server = " ncsi-netlink.service"
SYSTEMD_SERVICE_${PN}_append_mihawk = " ncsi-netlink.service"

do_install_append_ibm-ac-server() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/ncsi-netlink.service ${D}${systemd_system_unitdir}
}
do_install_append_mihawk() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/ncsi-netlink.service ${D}${systemd_system_unitdir}
}
