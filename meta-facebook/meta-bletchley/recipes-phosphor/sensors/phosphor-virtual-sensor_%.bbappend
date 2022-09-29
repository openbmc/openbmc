FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += " \
    file://virtual_sensor_config_hdc1080.json \
    file://virtual_sensor_config_si7021.json \
"

do_install:append() {
    install -m 0644 -D ${WORKDIR}/virtual_sensor_config_hdc1080.json ${D}${datadir}/phosphor-virtual-sensor/
    install -m 0644 -D ${WORKDIR}/virtual_sensor_config_si7021.json ${D}${datadir}/phosphor-virtual-sensor/
}
