FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://virtual_sensor_config.json"

do_install_append_rainier() {
    install -m 0644 ${WORKDIR}/virtual_sensor_config.json ${D}${datadir}/phosphor-virtual-sensor/
}
