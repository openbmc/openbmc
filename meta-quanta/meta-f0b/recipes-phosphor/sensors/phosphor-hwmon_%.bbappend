FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

EXTRA_OECONF_append_f0b = " --enable-negative-errno-on-fail"

NAME = " \
        bus@1e78a000/i2c-bus@80/tmp421@4c \
        bus@1e78a000/i2c-bus@1c0/tmp421@4c \
        bus@1e78a000/i2c-bus@180/tps53679@60\
        bus@1e78a000/i2c-bus@180/tps53659@62\
        bus@1e78a000/i2c-bus@180/tps53659@64\
        bus@1e78a000/i2c-bus@180/tps53679@70\
        bus@1e78a000/i2c-bus@180/tps53659@72\
        bus@1e78a000/i2c-bus@180/tps53659@74\
        bus@1e78a000/i2c-bus@180/ina219@40\
        bus@1e78a000/i2c-bus@180/ina219@41\
        "
ITEMSFMT = "ahb/apb/{0}.conf"

ITEMS = "${@compose_list(d, 'ITEMSFMT', 'NAME')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'ITEMS')}"
