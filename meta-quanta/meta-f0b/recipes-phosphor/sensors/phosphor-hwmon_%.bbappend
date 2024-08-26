FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

EXTRA_OEMESON:append:f0b = " -Dnegative-errno-on-fail=true"

NAME = " \
        bus@1e78a000/i2c@80/tmp421@4c \
        bus@1e78a000/i2c@1c0/tmp421@4c \
        bus@1e78a000/i2c@180/tps53679@60\
        bus@1e78a000/i2c@180/tps53659@62\
        bus@1e78a000/i2c@180/tps53659@64\
        bus@1e78a000/i2c@180/tps53679@70\
        bus@1e78a000/i2c@180/tps53659@72\
        bus@1e78a000/i2c@180/tps53659@74\
        bus@1e78a000/i2c@180/ina219@40\
        bus@1e78a000/i2c@180/ina219@41\
        "
ITEMSFMT = "ahb/apb@1e780000/{0}.conf"

ITEMS = "${@compose_list(d, 'ITEMSFMT', 'NAME')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN} += "${@compose_list(d, 'ENVS', 'ITEMS')}"
