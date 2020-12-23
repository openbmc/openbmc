FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

EXTRA_OECONF_append_fp5280g2= " --enable-negative-errno-on-fail"

CHIPS = " \
        bus@1e78a000/i2c-bus@c0/tmp112@48 \
        bus@1e78a000/i2c-bus@c0/tmp112@49 \
        bus@1e78a000/i2c-bus@c0/i2c-switch@70/i2c@0/tmp112@4a \
        bus@1e78a000/i2c-bus@c0/i2c-switch@70/i2c@1/tmp112@4a \
        bus@1e78a000/i2c-bus@c0/i2c-switch@70/i2c@2/tmp112@4a \
        bus@1e78a000/i2c-bus@400/power-supply@58 \
        bus@1e78a000/i2c-bus@400/power-supply@59 \
        pwm-tacho-controller@1e786000 \
        "
ITEMSFMT = "ahb/apb/{0}.conf"

ITEMS = "${@compose_list(d, 'ITEMSFMT', 'CHIPS')}"
ITEMS += "iio-hwmon.conf iio-hwmon-battery.conf"

OCCS = " \
       00--00--00--06/sbefifo1-dev0/occ-hwmon.1 \
       00--00--00--0a/fsi1/slave@01--00/01--01--00--06/sbefifo2-dev0/occ-hwmon.2 \
       "

OCCSFMT = "devices/platform/gpio-fsi/fsi-master/fsi0/slave@00--00/{0}.conf"
OCCITEMS = "${@compose_list(d, 'OCCSFMT', 'OCCS')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_fp5280g2 = " ${@compose_list(d, 'ENVS', 'ITEMS')}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_fp5280g2 = " ${@compose_list(d, 'ENVS', 'OCCITEMS')}"
