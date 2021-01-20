FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"
SRC_URI_append_gbs = " file://config-sku.json"
SRC_URI_append_gbs = " file://fan-table-init.sh"
SRC_URI_append_gbs = " file://fan-default-speed.sh"
SRC_URI_append_gbs = " file://phosphor-pid-control.service"
SRC_URI_append_gbs = " file://fan-reboot-control.service"

FILES_${PN}_append_gbs = " ${datadir}/swampd/config-sku.json"
FILES_${PN}_append_gbs = " ${bindir}/fan-default-speed.sh"
FILES_${PN}_append_gbs = " ${bindir}/fan-table-init.sh"

RDEPENDS_${PN} += "bash"

SYSTEMD_SERVICE_${PN}_append_gbs = " phosphor-pid-control.service"
SYSTEMD_SERVICE_${PN}_append_gbs = " fan-reboot-control.service"

do_install_append_gbs() {
    install -d ${D}/${bindir}
    install -m 0755 ${WORKDIR}/fan-default-speed.sh ${D}/${bindir}
    install -m 0755 ${WORKDIR}/fan-table-init.sh ${D}/${bindir}

    install -d ${D}${datadir}/swampd
    install -m 0644 -D ${WORKDIR}/config-sku.json \
        ${D}${datadir}/swampd/config-sku.json

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/phosphor-pid-control.service \
        ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/fan-reboot-control.service \
        ${D}${systemd_system_unitdir}
}
