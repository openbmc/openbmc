FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"
SRC_URI_append_gbs = " file://config.json.in \
                       file://fan-table-init.sh \
                       file://phosphor-pid-control.service \
                     "

FILES_${PN}_append_gbs = " ${datadir}/swampd/config.json.in"
FILES_${PN}_append_gbs = " ${bindir}/fan-table-init.sh"

RDEPENDS_${PN} += "bash"

SYSTEMD_SERVICE_${PN}_append_gbs = " phosphor-pid-control.service"

do_install_append_gbs() {
    install -d ${D}/${bindir}
    install -m 0755 ${WORKDIR}/fan-table-init.sh ${D}/${bindir}

    install -d ${D}${datadir}/swampd
    install -m 0644 -D ${WORKDIR}/config.json.in \
        ${D}${datadir}/swampd/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/phosphor-pid-control.service \
        ${D}${systemd_system_unitdir}
}
