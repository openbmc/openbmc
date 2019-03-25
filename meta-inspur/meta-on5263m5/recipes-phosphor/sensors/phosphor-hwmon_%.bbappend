FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

EXTRA_OECONF_append_on5263m5= " --enable-negative-errno-on-fail"

CHIPS = " \
        bus@1e78a000/i2c-bus@1c0/tmp112@48 \
        bus@1e78a000/i2c-bus@1c0/tmp421@4e \
        bus@1e78a000/i2c-bus@300/adm1278@11 \
        pwm-tacho-controller@1e786000 \
        "
ITEMSFMT = "ahb/apb/{0}.conf"

ITEMS = "${@compose_list(d, 'ITEMSFMT', 'CHIPS')}"

ITEMS += "iio-hwmon.conf"


ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'ITEMS')}"
