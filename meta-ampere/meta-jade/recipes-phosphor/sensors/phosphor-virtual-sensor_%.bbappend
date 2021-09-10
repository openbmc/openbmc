FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:mtjade += " \
            file://virtual_sensor_config.json \
           "

do_install:append:mtjade() {
    install -m 0644 ${WORKDIR}/virtual_sensor_config.json ${D}${datadir}/phosphor-virtual-sensor/
}

