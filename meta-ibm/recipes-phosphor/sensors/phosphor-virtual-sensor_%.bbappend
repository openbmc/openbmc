FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://virtual_sensor_config.json"

do_install:append:p10bmc() {
    install -m 0644 ${WORKDIR}/virtual_sensor_config.json ${D}${datadir}/phosphor-virtual-sensor/
}
