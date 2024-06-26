FILESEXTRAPATHS:prepend:gbs := "${THISDIR}/${PN}:"

SRC_URI:append:gbs = " file://config-virtual-sensor.json \
                       file://phosphor-virtual-sensor.service.replace \
                     "

RDEPENDS:${PN}:append:gbs = "bash"

do_install:append:gbs() {
    install -d ${D}${datadir}/${PN}
    install -m 0644 -D ${UNPACKDIR}/config-virtual-sensor.json \
        ${D}${datadir}/${PN}/virtual_sensor_config.json

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/${PN}.service.replace \
        ${D}${systemd_system_unitdir}/${PN}.service
}
