#"Copyright (c) 2019-present Lenovo
#Licensed under BSD-3, see COPYING.BSD file for details."

FILESEXTRAPATHS_prepend_hr630 := "${THISDIR}/${PN}:"
EXTRA_OECONF_append_hr630 = " --enable-negative-errno-on-fail"

CHIPS = " \
        bus@1e78a000/i2c-bus@40/tmp75@4e \
        bus@1e78a000/i2c-bus@80/tmp75@4d \
        pwm-tacho-controller@1e786000 \
        "
ITEMSFMT = "ahb/apb/{0}.conf"

ITEMS = "${@compose_list(d, 'ITEMSFMT', 'CHIPS')}"
ITEMS += "iio-hwmon.conf"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_hr630 := "${@compose_list(d, 'ENVS', 'ITEMS')}"
