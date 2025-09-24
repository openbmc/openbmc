FILESEXTRAPATHS:prepend:palmetto := "${THISDIR}/${PN}:"

NAMES = " \
        bus@1e78a000/i2c@40/rtc@68 \
        bus@1e78a000/i2c@c0/tmp423@4c \
        bus@1e78a000/i2c@100/occ-hwmon@50 \
        "
ITEMSFMT = "ahb/apb/{0}.conf"

ITEMS = "${@compose_list(d, 'ITEMSFMT', 'NAMES')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:palmetto = " ${@compose_list(d, 'ENVS', 'ITEMS')}"
