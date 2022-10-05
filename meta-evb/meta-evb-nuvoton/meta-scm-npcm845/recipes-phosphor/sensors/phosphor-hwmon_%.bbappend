FILESEXTRAPATHS:prepend:scm-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:scm-npcm845 = " \
  file://start_hwmon.sh \
  file://0001-support-hwmon-sysfs-in-sys-devices-virtual.patch \
  "

NAMES = " \
        i2c@86000/tmp100@48 \
        "

ITEMSFMT = "ahb/apb/{0}.conf"

ITEMS += "${@compose_list(d, 'ITEMSFMT', 'NAMES')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN} += "${@compose_list(d, 'ENVS', 'ITEMS')}"

# Fan sensors
FITEMS = "pwm-fan-controller@103000.conf"
FENVS = "obmc/hwmon/ahb/apb/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN} += "${@compose_list(d, 'FENVS', 'FITEMS')}"

# ADC
ADC_ITEMS = "adc@c000.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN} += "${@compose_list(d, 'FENVS', 'ADC_ITEMS')}"

# thermal_zone
SYSTEMD_ENVIRONMENT_FILE:${PN} += " \
         obmc/hwmon/devices/virtual/thermal/thermal_zone0.conf \
         obmc/hwmon/devices/virtual/thermal/thermal_zone1.conf \
         "

do_install:append:scm-npcm845() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/start_hwmon.sh ${D}${bindir}
}

