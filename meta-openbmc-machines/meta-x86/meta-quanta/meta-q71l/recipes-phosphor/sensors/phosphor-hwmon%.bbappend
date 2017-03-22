FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# More conf files can be added as sensors are added.

ITEMS = "pwm-tacho-controller@1e786000.conf"

TEMPS = "tmp75@4c tmp75@4e tmp75@4f"
TEMPBASE = "apb/i2c@1e78a000/i2c-bus@80/{0}.conf"
ITEMS += "${@compose_list(d, 'TEMPBASE', 'TEMPS')}"

ENVS = "obmc/hwmon/ahb/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'ITEMS')}"
