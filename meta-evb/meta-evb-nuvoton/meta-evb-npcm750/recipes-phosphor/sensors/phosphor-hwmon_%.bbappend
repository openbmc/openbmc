FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

NAMES = " \
        i2c@82000/tmp100@48 \
        i2c@81000/lm75@48 \
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
