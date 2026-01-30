FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:ventura = " file://virtual_sensor_config.json "

inherit obmc-phosphor-systemd
RDEPENDS:${PN}:append:ventura2 = " bash"

SRC_URI:append:ventura2 = " \
	file://virtual_sensor_config.json \
	file://check-interposer-config \
	file://check-interposer-config.conf \
        file://ventura2_sku1.json \
	file://ventura2_sku2.json \
	"

FILES:${PN}:append:ventura2 = "\
    ${libexecdir}/phosphor-virtual-sensor \
    ${datadir}/phosphor-virtual-sensor/ventura2_sku2.json \
    "

SYSTEMD_OVERRIDE:${PN}:append:ventura2 = "\
    check-interposer-config.conf:phosphor-virtual-sensor.service.d/check-interposer-config.conf \
    "

do_install:append:ventura2() {
    install -d ${D}${datadir}/phosphor-virtual-sensor
    install -m 0644 -D ${UNPACKDIR}/ventura2_sku1.json ${D}${datadir}/phosphor-virtual-sensor/ventura2_sku1.json
    install -m 0644 -D ${UNPACKDIR}/ventura2_sku2.json ${D}${datadir}/phosphor-virtual-sensor/ventura2_sku2.json

    LIBEXECDIR="${D}${libexecdir}/phosphor-virtual-sensor"
    install -d ${LIBEXECDIR}
    install -m 0755 -D ${UNPACKDIR}/check-interposer-config ${LIBEXECDIR}/check-interposer-config
}
