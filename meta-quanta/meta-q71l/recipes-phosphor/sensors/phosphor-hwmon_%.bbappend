inherit obmc-phosphor-utils

FILESEXTRAPATHS:prepend:quanta-q71l := "${THISDIR}/${PN}:"

# More conf files can be added as sensors are added.

Q71L_PWM = "pwm-tacho-controller@1e786000"
Q71L_PWM_BASE = "ahb/apb@1e780000/{0}.conf"
Q71L_ITEMS += "${@compose_list(d, 'Q71L_PWM_BASE', 'Q71L_PWM')}"

Q71L_TEMPS = "tmp75@4c tmp75@4e tmp75@4f"
Q71L_TEMPBASE = "ahb/apb@1e780000/bus@1e78a000/i2c@80/{0}.conf"

Q71L_ITEMS += "${@compose_list(d, 'Q71L_TEMPBASE', 'Q71L_TEMPS')}"

Q71L_ITEMS += "iio-hwmon.conf"
Q71L_ITEMS += "iio-hwmon-battery.conf"

Q71L_PSUS = "0/psu@59 1/psu@58 2/psu@58 3/psu@59"
Q71L_PSUBASE = "ahb/apb@1e780000/bus@1e78a000/i2c@300/i2c-switch@70/i2c@{0}.conf"
Q71L_ITEMS += "${@compose_list(d, 'Q71L_PSUBASE', 'Q71L_PSUS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:quanta-q71l := " ${@compose_list(d, 'ENVS', 'Q71L_ITEMS')}"

EXTRA_OEMESON:append:quanta-q71l = " -Dnegative-errno-on-fail=true"
