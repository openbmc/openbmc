FILESEXTRAPATHS_append_mtjade := "${THISDIR}/${PN}:"

EXTRA_OECONF_append_mtjade = " --enable-negative-errno-on-fail"

SRC_URI += " \
        file://0001-sensor-create-the-concerned-host-sensors-list.patch \
        file://0002-mainloop-activate-deactivate-the-host-sensors.patch \
	file://0003-sensors-activate-deactivate-the-host-power-domain-se.patch \
        "

CHIPS_mtjade = " \
        bus@1e78a000/i2c-bus@100/tmp175@28 \
        bus@1e78a000/i2c-bus@100/tmp175@29 \
        bus@1e78a000/i2c-bus@100/tmp175@2a \
        bus@1e78a000/i2c-bus@100/tmp175@2b \
        bus@1e78a000/i2c-bus@100/tmp175@2c \
        bus@1e78a000/i2c-bus@100/tmp175@2d \
        bus@1e78a000/i2c-bus@c0/smpro@4f \
        bus@1e78a000/i2c-bus@c0/smpro@4e \
        bus@1e78a000/i2c-bus@1c0/psu@58 \
        bus@1e78a000/i2c-bus@1c0/psu@59 \
        pwm-tacho-controller@1e786000 \
        "

ITEMSFMT = "ahb/apb/{0}.conf"

ITEMS = "${@compose_list(d, 'ITEMSFMT', 'CHIPS_${MACHINE}')}"

ITEMS += "iio-hwmon.conf iio-hwmon-adc14.conf iio-hwmon-battery.conf"

ENVS = "obmc/${MACHINE}/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_mtjade = " ${@compose_list(d, 'ENVS', 'ITEMS')}"

do_install_append_mtjade() {
  SOURCEDIR="${WORKDIR}/obmc/${MACHINE}/hwmon"
  DESTDIR="${D}${sysconfdir}/default/obmc"
  install -d ${DESTDIR}
  cp -r ${SOURCEDIR} ${DESTDIR}
}
