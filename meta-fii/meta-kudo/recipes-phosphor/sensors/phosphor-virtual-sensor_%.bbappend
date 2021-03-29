FILESEXTRAPATHS_prepend_kudo := "${THISDIR}/${PN}:"

inherit systemd
RDEPENDS_${PN} += "bash"
SYSTEMD_SERVICE_${PN}_append_kudo = " tla2024-enable.service"

SRC_URI_append_kudo = " \
    file://virtual_sensor_config.json \
    file://tla2024-enable.service \
    file://tla2024-enable.sh \
    "
FILES_${PN}_append_kudo = " ${bindir}/tla2024-enable.sh"

do_install_append_kudo() {
    install -d ${D}${datadir}/${PN}
    install -m 0644 ${WORKDIR}/virtual_sensor_config.json ${D}${datadir}/${PN}/virtual_sensor_config.json
    install -m 0755 ${WORKDIR}/tla2024-enable.sh ${D}${bindir}/tla2024-enable.sh

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/tla2024-enable.service ${D}${systemd_system_unitdir}/tla2024-enable.service
}
