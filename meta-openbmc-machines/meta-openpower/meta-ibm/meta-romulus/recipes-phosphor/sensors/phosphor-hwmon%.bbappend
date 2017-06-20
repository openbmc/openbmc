FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

OCCS = " \
       sbefifo@2400/occ@1/occ-hwmon@1 \
       hub@3400/cfam@1,0/sbefifo@2400/occ@2/occ-hwmon@2 \
       "

OCCSFMT = "gpio-fsi/cfam@0,0/{0}.conf"
OCCITEMS = "${@compose_list(d, 'OCCSFMT', 'OCCS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'OCCITEMS')}"
