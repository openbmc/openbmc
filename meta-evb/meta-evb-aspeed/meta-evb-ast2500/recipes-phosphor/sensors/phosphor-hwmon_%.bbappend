FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

CHIPS = " \
        i2c@1e78a000/i2c-bus@300/lm75@4b \
        pwm-tacho-controller@1e786000 \
        "
ITEMSFMT = "ahb/apb/{0}.conf"

ITEMS = "${@compose_list(d, 'ITEMSFMT', 'CHIPS')}"

ITEMS += "iio-hwmon-12v.conf iio-hwmon-33vrun.conf iio-hwmon-33vdual.conf iio-hwmon-5vrun.conf iio-hwmon-5vdual.conf"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'ITEMS')}"

EXTRA_OECONF = "--enable-negative-errno-on-fail "
