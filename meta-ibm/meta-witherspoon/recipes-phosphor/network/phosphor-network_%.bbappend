FILESEXTRAPATHS_prepend := "${THISDIR}/network:"
SRC_URI += "file://ncsi-netlink.service"
SYSTEMD_SERVICE_${PN} += "ncsi-netlink.service"

do_install_append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/ncsi-netlink.service ${D}${systemd_system_unitdir}
}
