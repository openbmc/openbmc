FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"

SRC_URI_append_gbs = " file://config-virtual-sensor.json \
                       file://phosphor-virtual-sensor.service.replace \
                     "

do_install_append_gbs() {
    install -d ${D}${datadir}/${PN}
    install -m 0644 -D ${WORKDIR}/config-virtual-sensor.json \
        ${D}${datadir}/${PN}/virtual_sensor_config.json

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/${PN}.service.replace \
        ${D}${systemd_system_unitdir}/${PN}.service
}
