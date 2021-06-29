FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append_mtjade += " \
            file://virtual_sensor_config.json \
           "

do_install_append_mtjade() {
    install -m 0644 ${WORKDIR}/virtual_sensor_config.json ${D}${datadir}/phosphor-virtual-sensor/
}

