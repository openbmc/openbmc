FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

I2CCHIPS = " \
           i2c@1e78a000/i2c-bus@100/bmp280@77 \
           i2c@1e78a000/i2c-bus@100/dps310@76 \
           i2c@1e78a000/i2c-bus@140/ir35221@70 \
           i2c@1e78a000/i2c-bus@140/ir35221@71 \
           i2c@1e78a000/i2c-bus@180/ir35221@70 \
           i2c@1e78a000/i2c-bus@180/ir35221@71 \
           "
I2CCHIPSFMT = "ahb/apb/{0}.conf"
I2CITEMS = "${@compose_list(d, 'I2CCHIPSFMT', 'I2CCHIPS')}"

OCCS = " \
       cfam@0,0/sbefifo@2400/occ@1 \
       "
OCCSFMT = "base/gpio-fsi/{0}.conf"
OCCITEMS = "${@compose_list(d, 'OCCSFMT', 'OCCS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'I2CITEMS')}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'OCCITEMS')}"
