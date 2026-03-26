FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://virtual_sensor_config.json "

#=========================================================
# Clemente interposer
#=========================================================
RDEPENDS:${PN}:append:clemente = " bash"

SRC_URI:append:clemente = " \
    file://check-interposer-config \
    file://check-interposer-config.conf \
    file://no-cable-tsense.json \
    "

FILES:${PN}:append:clemente = "\
    ${libexecdir}/phosphor-virtual-sensor \
    ${datadir}/phosphor-virtual-sensor/no-cable-tsense.json \
    ${systemd_system_unitdir}/phosphor-virtual-sensor.service.d/check-interposer-config.conf \
    "

do_install:append:clemente() {
    install -d ${D}${datadir}/phosphor-virtual-sensor
    install -m 0644 -D ${UNPACKDIR}/no-cable-tsense.json ${D}${datadir}/phosphor-virtual-sensor/no-cable-tsense.json

    LIBEXECDIR="${D}${libexecdir}/phosphor-virtual-sensor"
    install -d ${LIBEXECDIR}
    install -m 0755 -D ${UNPACKDIR}/check-interposer-config ${LIBEXECDIR}/check-interposer-config

    install -d ${D}${systemd_system_unitdir}/phosphor-virtual-sensor.service.d
    install -m 0644 ${UNPACKDIR}/check-interposer-config.conf \
        ${D}${systemd_system_unitdir}/phosphor-virtual-sensor.service.d/check-interposer-config.conf
}
