FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${BPN}:"

inherit systemd
RDEPENDS:${PN} += "bash"

SRC_URI:append:kudo = " \
    file://virtual_sensor_config.json \
    "
do_install:append:kudo() {
    install -d ${D}${datadir}
    install -m 0644 ${UNPACKDIR}/virtual_sensor_config.json ${D}${datadir}/phosphor-virtual-sensor/virtual_sensor_config.json
}

