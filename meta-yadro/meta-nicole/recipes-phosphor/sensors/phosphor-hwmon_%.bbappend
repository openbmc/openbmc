FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

EXTRA_OECONF_append = " --enable-negative-errno-on-fail"

OCCS = " \
       00--00--00--06/sbefifo1-dev0/occ-hwmon.1 \
       00--00--00--0a/fsi-master/fsi1/slave@01--00/01--01--00--06/sbefifo2-dev0/occ-hwmon.2 \
       "

OCCSFMT = "devices/platform/gpio-fsi/fsi-master/fsi0/slave@00--00/{0}.conf"
OCCITEMS = "${@compose_list(d, 'OCCSFMT', 'OCCS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append = " ${@compose_list(d, 'ENVS', 'OCCITEMS')}"
