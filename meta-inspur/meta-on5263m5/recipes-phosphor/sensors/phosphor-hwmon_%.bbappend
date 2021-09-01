FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

EXTRA_OEMESON:append:on5263m5= " -Dnegative-errno-on-fail=true"

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
SYSTEMD_ENVIRONMENT_FILE:${PN} += "${@compose_list(d, 'ENVS', 'ITEMS')}"
