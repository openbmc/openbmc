FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

OCCS = " \
       cfam@0,0/sbefifo@2400/occ@1 \
       cfam@0,0/sbefifo@82400/occ@2 \
       "

OCCSFMT = "base/gpio-fsi/{0}.conf"
OCCITEMS = "${@compose_list(d, 'OCCSFMT', 'OCCS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'OCCITEMS')}"
