FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}/${MACHINE}:"
SRC_URI:append:p10bmc = " file://virtual_sensor_config.json"

do_install:append:p10bmc() {
    install -m 0644 ${WORKDIR}/virtual_sensor_config.json ${D}${datadir}/phosphor-virtual-sensor/
}
