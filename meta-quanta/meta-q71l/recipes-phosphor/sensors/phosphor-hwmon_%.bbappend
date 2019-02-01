inherit obmc-phosphor-utils

FILESEXTRAPATHS_prepend_quanta-q71l := "${THISDIR}/${PN}:"

# More conf files can be added as sensors are added.

Q71L_ITEMS = "pwm-tacho-controller@1e786000.conf"

Q71L_TEMPS = "tmp75@4c tmp75@4e tmp75@4f"
Q71L_TEMPBASE = "ahb/apb/i2c@1e78a000/i2c-bus@80/{0}.conf"

Q71L_ITEMS += "${@compose_list(d, 'Q71L_TEMPBASE', 'Q71L_TEMPS')}"

Q71L_ITEMS += "iio-hwmon.conf"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_quanta-q71l := " ${@compose_list(d, 'ENVS', 'Q71L_ITEMS')}"

EXTRA_OECONF_append_quanta-q71l = " --enable-negative-errno-on-fail"
