FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${PN}:"

inherit systemd
RDEPENDS:${PN} += "bash"
SYSTEMD_SERVICE:${PN}:append:kudo = " cpu_detect_virtual.service \
                                    "

SRC_URI:append:kudo = " \
    file://virtual_sensor_config1p.json \
    file://virtual_sensor_config2p.json \
    file://cpu_detect_virtual.sh \
    file://cpu_detect_virtual.service \
    "
do_install:append:kudo() {
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${WORKDIR}/cpu_detect_virtual.sh ${D}${libexecdir}/${PN}/cpu_detect_virtual.sh
    install -d ${D}${sysconfdir}/virtual-sensor/configurations/
    install -m 0644 ${WORKDIR}/virtual_sensor_config1p.json ${D}${sysconfdir}/virtual-sensor/configurations/virtual_sensor_config1p.json
    install -m 0644 ${WORKDIR}/virtual_sensor_config2p.json ${D}${sysconfdir}/virtual-sensor/configurations/virtual_sensor_config2p.json
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/cpu_detect_virtual.service ${D}${systemd_system_unitdir}/cpu_detect_virtual.service
}

