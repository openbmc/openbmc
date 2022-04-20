FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${PN}:"

inherit systemd
RDEPENDS:${PN} += "bash"

SRC_URI:append:kudo = " \
    file://virtual_sensor_config.json \
    "
do_install:append:kudo() {
    install -d ${D}/usr/share
    install -m 0644 ${WORKDIR}/virtual_sensor_config.json ${D}/usr/share/phosphor-virtual-sensor/virtual_sensor_config.json
}

