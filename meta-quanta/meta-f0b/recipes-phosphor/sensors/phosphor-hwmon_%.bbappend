FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

EXTRA_OECONF_append_f0b = " --enable-negative-errno-on-fail"

NAME = " \
        i2c@1e78a000/i2c-bus@80/tmp421@4c \
        i2c@1e78a000/i2c-bus@1c0/tmp421@4c \
        i2c@1e78a000/i2c-bus@180/tps53679@60\
        i2c@1e78a000/i2c-bus@180/tps53659@62\
        i2c@1e78a000/i2c-bus@180/tps53659@64\
        i2c@1e78a000/i2c-bus@180/tps53679@70\
        i2c@1e78a000/i2c-bus@180/tps53659@72\
        i2c@1e78a000/i2c-bus@180/tps53659@74\
        i2c@1e78a000/i2c-bus@180/ina219@40\
        i2c@1e78a000/i2c-bus@180/ina219@41\
        "
ITEMSFMT = "ahb/apb/{0}.conf"

ITEMS = "${@compose_list(d, 'ITEMSFMT', 'NAME')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'ITEMS')}"
