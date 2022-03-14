FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${PN}:"

inherit systemd
RDEPENDS:${PN} += "bash"
SYSTEMD_SERVICE:${PN}:append:kudo = " tla2024-enable.service \
                                      cpu_detect_virtual.service \
                                    "

SRC_URI:append:kudo = " \
    file://virtual_sensor_config1p2G.json \
    file://virtual_sensor_config2p2G.json \
    file://virtual_sensor_config1p4G.json \
    file://tla2024-enable.service \
    file://tla2024-enable.sh \
    file://cpu_detect_virtual.sh \
    file://cpu_detect_virtual.service \
    "
do_install:append:kudo() {
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${WORKDIR}/tla2024-enable.sh ${D}${libexecdir}/${PN}/tla2024-enable.sh
    install -d ${D}${sysconfdir}/virtual-sensor/configurations/
    install -m 0644 ${WORKDIR}/virtual_sensor_config1p2G.json ${D}${sysconfdir}/virtual-sensor/configurations/virtual_sensor_config1p2G.json
    install -m 0644 ${WORKDIR}/virtual_sensor_config2p2G.json ${D}${sysconfdir}/virtual-sensor/configurations/virtual_sensor_config2p2G.json
    install -m 0644 ${WORKDIR}/virtual_sensor_config1p4G.json ${D}${sysconfdir}/virtual-sensor/configurations/virtual_sensor_config1p4G.json
    install -m 0755 ${WORKDIR}/cpu_detect_virtual.sh ${D}${libexecdir}/${PN}/cpu_detect_virtual.sh
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/tla2024-enable.service ${D}${systemd_system_unitdir}/tla2024-enable.service
    install -m 0644 ${WORKDIR}/cpu_detect_virtual.service ${D}${systemd_system_unitdir}/cpu_detect_virtual.service
}

