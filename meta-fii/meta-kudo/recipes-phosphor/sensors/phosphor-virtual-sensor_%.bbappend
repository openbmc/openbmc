FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${PN}:"

inherit systemd
RDEPENDS:${PN} += "bash"
SYSTEMD_SERVICE:${PN}:append:kudo = " tla2024-enable.service"

SRC_URI:append:kudo = " \
    file://virtual_sensor_config.json \
    file://tla2024-enable.service \
    file://tla2024-enable.sh \
    "
FILES:${PN}:append:kudo = " ${bindir}/tla2024-enable.sh"

do_install:append:kudo() {
    install -d ${D}${datadir}/${PN}
    install -m 0644 ${WORKDIR}/virtual_sensor_config.json ${D}${datadir}/${PN}/virtual_sensor_config.json
    install -m 0755 ${WORKDIR}/tla2024-enable.sh ${D}${bindir}/tla2024-enable.sh

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/tla2024-enable.service ${D}${systemd_system_unitdir}/tla2024-enable.service
}
