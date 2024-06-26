FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append:p10bmc = " file://virtual_sensor_config.json"
SRC_URI:append:sbp1 = " file://virtual_sensor_config.json"

do_install:append:sbp1() {
    install -m 0644 ${UNPACKDIR}/virtual_sensor_config.json ${D}${datadir}/phosphor-virtual-sensor/
}

do_install:append:p10bmc() {
    install -m 0644 ${UNPACKDIR}/virtual_sensor_config.json ${D}${datadir}/phosphor-virtual-sensor/
}
