FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

EXTRA_OECONF_append_fp5280g2= " --enable-negative-errno-on-fail"

CHIPS = " \
        bus@1e78a000/i2c-bus@c0/tmp112@48 \
        bus@1e78a000/i2c-bus@c0/tmp112@49 \
        bus@1e78a000/i2c-bus@c0/i2c-switch@70/i2c@0/tmp112@4a \
        bus@1e78a000/i2c-bus@c0/i2c-switch@70/i2c@1/tmp112@4a \
        bus@1e78a000/i2c-bus@c0/i2c-switch@70/i2c@2/tmp112@4a \
        pwm-tacho-controller@1e786000 \
        "
ITEMSFMT = "ahb/apb/{0}.conf"

ITEMS = "${@compose_list(d, 'ITEMSFMT', 'CHIPS')}"
ITEMS += "iio-hwmon.conf iio-hwmon-battery.conf"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'ITEMS')}"
