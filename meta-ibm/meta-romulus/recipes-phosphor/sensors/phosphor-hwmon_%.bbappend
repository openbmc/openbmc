FILESEXTRAPATHS_prepend_romulus := "${THISDIR}/${PN}:"

EXTRA_OECONF_append_romulus = " --enable-negative-errno-on-fail"

CHIPS = " \
        bus@1e78a000/i2c-bus@440/w83773g@4c \
        pwm-tacho-controller@1e786000 \
        "
ITEMSFMT = "ahb/apb/{0}.conf"

ITEMS = "${@compose_list(d, 'ITEMSFMT', 'CHIPS')}"

ITEMS += "iio-hwmon-battery.conf"

OCCS = " \
       00--00--00--06/sbefifo1-dev0/occ-hwmon.1 \
       00--00--00--0a/fsi1/slave@01--00/01--01--00--06/sbefifo2-dev0/occ-hwmon.2 \
       "

OCCSFMT = "devices/platform/gpio-fsi/fsi0/slave@00--00/{0}.conf"
OCCITEMS = "${@compose_list(d, 'OCCSFMT', 'OCCS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_romulus = " ${@compose_list(d, 'ENVS', 'ITEMS')}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_romulus = " ${@compose_list(d, 'ENVS', 'OCCITEMS')}"
