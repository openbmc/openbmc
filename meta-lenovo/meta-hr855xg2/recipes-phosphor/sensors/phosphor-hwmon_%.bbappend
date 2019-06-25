#"Copyright (c) 2019-present Lenovo
#Licensed under BSD-3, see COPYING.BSD file for details."

FILESEXTRAPATHS_prepend_hr855xg2 := "${THISDIR}/${PN}:"

CHIPS = " bus@1e78a000/i2c-bus@1c0/tmp75@49 bus@1e78a000/i2c-bus@1c0/tmp75@4d pwm-tacho-controller@1e786000 "
ITEMSFMT = "ahb/apb/{0}.conf"

HR855XG2_ITEMS = "${@compose_list(d, 'ITEMSFMT', 'CHIPS')}"
HR855XG2_ITEMS += "iio-hwmon.conf"

HR855XG2_ITEMS += "iio-hwmon-battery.conf"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_hr855xg2 := "${@compose_list(d, 'ENVS', 'HR855XG2_ITEMS')}"
