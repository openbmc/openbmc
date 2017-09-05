FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

ZAIUS_CHIPS = "i2c@1e78a000/i2c-bus@40/ucd90160@64"
ZAIUS_ITEMSFMT = "ahb/apb/{0}.conf"

ZAIUS_ITEMS = "${@compose_list(d, 'ZAIUS_ITEMSFMT', 'ZAIUS_CHIPS')}"

ZAIUS_OCCS = " \
              00--00--00--06/sbefifo1-dev0/occ-hwmon.1 \
              00--00--00--0a/fsi1/slave@01--00/01--01--00--06/sbefifo2-dev0/occ-hwmon.2 \
              "
ZAIUS_OCCSFMT = "devices/platform/gpio-fsi/fsi0/slave@00--00/{0}.conf"
ZAIUS_OCCITEMS = "${@compose_list(d, 'ZAIUS_OCCSFMT', 'ZAIUS_OCCS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'ZAIUS_ITEMS')}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'ZAIUS_OCCITEMS')}"
