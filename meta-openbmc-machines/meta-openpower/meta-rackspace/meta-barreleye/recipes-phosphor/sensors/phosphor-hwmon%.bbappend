FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

CHIPS = " \
        i2c@1e78a000/i2c-bus@40/lm75@4a \
        i2c@1e78a000/i2c-bus@40/rtc@68 \
        i2c@1e78a000/i2c-bus@1c0/nct7904@2e \
        i2c@1e78a000/i2c-bus@1c0/nct7904@2d \
        "

ITEMSFMT = "ahb/apb/{0}.conf"

ITEMS = "${@compose_list(d, 'ITEMSFMT', 'CHIPS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'ITEMS')}"
