FILESEXTRAPATHS:prepend:s6q := "${THISDIR}/${PN}:"

SRC_URI:append:s6q = " file://virtual_sensor_config.json "

do_install:append:s6q() {
    install -m 0644 -D ${UNPACKDIR}/virtual_sensor_config.json ${D}${datadir}/phosphor-virtual-sensor/
}
