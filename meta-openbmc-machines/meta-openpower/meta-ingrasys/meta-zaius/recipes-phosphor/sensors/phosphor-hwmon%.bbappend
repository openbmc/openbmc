FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

NAMES = "i2c@1e78a000/i2c-bus@40/ucd90160@64"
ITEMSFMT = "ahb/apb/{0}.conf"

ITEMS = "${@compose_list(d, 'ITEMSFMT', 'NAMES')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'ITEMS')}"
