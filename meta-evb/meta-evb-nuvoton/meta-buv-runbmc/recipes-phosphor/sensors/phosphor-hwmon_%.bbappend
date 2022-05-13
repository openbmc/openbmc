FILESEXTRAPATHS:prepend:buv-runbmc := "${THISDIR}/${PN}:"


NAMES = " \
        i2c@8d000/tmp75@4a \
        i2c@8c000/tmp75@48 \
        i2c@8b000/ina219@40 \
        i2c@8b000/ina219@41 \
        "

ITEMSFMT = "ahb/apb/{0}.conf"

ITEMS += "${@compose_list(d, 'ITEMSFMT', 'NAMES')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:buv-runbmc = " ${@compose_list(d, 'ENVS', 'ITEMS')}"

# Fan sensors
FITEMS = "pwm-fan-controller@103000.conf"
FENVS = "obmc/hwmon/ahb/apb/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:buv-runbmc = " ${@compose_list(d, 'FENVS', 'FITEMS')}"

# ADC
ADC_ITEMS = "adc@c000.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:buv-runbmc = " ${@compose_list(d, 'FENVS', 'ADC_ITEMS')}"
