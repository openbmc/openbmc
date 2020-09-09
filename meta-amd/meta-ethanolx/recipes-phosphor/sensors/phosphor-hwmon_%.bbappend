FILESEXTRAPATHS_prepend_ethanolx := "${THISDIR}/${PN}:"
EXTRA_OECONF_append_ethanolx = " --enable-negative-errno-on-fail"


CHIPS = " \
        bus@1e78a000/i2c-bus@300/lm75a@48 \
        bus@1e78a000/i2c-bus@300/lm75a@49 \
        bus@1e78a000/i2c-bus@300/lm75a@4a \
        bus@1e78a000/i2c-bus@300/lm75a@4b \
        bus@1e78a000/i2c-bus@300/lm75a@4c \
        bus@1e78a000/i2c-bus@300/lm75a@4d \
        bus@1e78a000/i2c-bus@300/lm75a@4e \
        bus@1e78a000/i2c-bus@300/lm75a@4f \
        pwm-tacho-controller@1e786000 \
        "
ITEMSFMT = "ahb/apb/{0}.conf"
ITEMS = "${@compose_list(d, 'ITEMSFMT', 'CHIPS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_ethanolx = " ${@compose_list(d, 'ENVS', 'ITEMS')}"
