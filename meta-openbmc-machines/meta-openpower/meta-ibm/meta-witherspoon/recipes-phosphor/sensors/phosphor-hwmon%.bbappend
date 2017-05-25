FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

WSPOON_CHIPS = " \
               i2c@1e78a000/i2c-bus@100/bmp280@77 \
               i2c@1e78a000/i2c-bus@100/dps310@76 \
               i2c@1e78a000/i2c-bus@140/ir35221@70 \
               i2c@1e78a000/i2c-bus@140/ir35221@71 \
               i2c@1e78a000/i2c-bus@180/ir35221@70 \
               i2c@1e78a000/i2c-bus@180/ir35221@71 \
               "

WSPOON_ITEMSFMT = "ahb/apb/{0}.conf"

WSPOON_ITEMS = "${@compose_list(d, 'WSPOON_ITEMSFMT', 'WSPOON_CHIPS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'WSPOON_ITEMS')}"
