FILESEXTRAPATHS:prepend:zaius := "${THISDIR}/${PN}:"

ZAIUS_CHIPS = "bus@1e78a000/i2c@40/ucd90160@64"
ZAIUS_CHIPS += " bus@1e78a000/i2c@300/pca9541a@70/i2c-arb/hotswap@54"
ZAIUS_CHIPS += " pwm-tacho-controller@1e786000"
ZAIUS_CHIPS += " bus@1e78a000/i2c@300/power-brick@30"
ZAIUS_CHIPS += " bus@1e78a000/i2c@300/power-brick@6a"
ZAIUS_CHIPS += " bus@1e78a000/i2c@300/vrm@40"
ZAIUS_CHIPS += " bus@1e78a000/i2c@300/vrm@41"
ZAIUS_CHIPS += " bus@1e78a000/i2c@300/vrm@43"
ZAIUS_CHIPS += " bus@1e78a000/i2c@300/vrm@60"
ZAIUS_CHIPS += " bus@1e78a000/i2c@300/vrm@64"
ZAIUS_CHIPS += " bus@1e78a000/i2c@340/vrm@40"
ZAIUS_CHIPS += " bus@1e78a000/i2c@340/vrm@41"
ZAIUS_CHIPS += " bus@1e78a000/i2c@340/vrm@42"
ZAIUS_CHIPS += " bus@1e78a000/i2c@340/vrm@60"
ZAIUS_CHIPS += " bus@1e78a000/i2c@340/vrm@64"
ZAIUS_ITEMSFMT = "ahb/apb@1e780000/{0}.conf"

ZAIUS_ITEMS = "${@compose_list(d, 'ZAIUS_ITEMSFMT', 'ZAIUS_CHIPS')}"
ZAIUS_ITEMS += "iio-hwmon.conf iio-hwmon-battery.conf"
ZAIUS_ITEMS += " onewire0.conf"
ZAIUS_ITEMS += " onewire1.conf"
ZAIUS_ITEMS += " onewire2.conf"
ZAIUS_ITEMS += " onewire3.conf"

ZAIUS_OCCS = " \
              00--00--00--06/sbefifo1-dev0/occ-hwmon.1 \
              00--00--00--0a/fsi1/slave@01--00/01--01--00--06/sbefifo2-dev0/occ-hwmon.2 \
              "
ZAIUS_OCCSFMT = "devices/platform/gpio-fsi/fsi0/slave@00--00/{0}.conf"
ZAIUS_OCCITEMS = "${@compose_list(d, 'ZAIUS_OCCSFMT', 'ZAIUS_OCCS')}"

ENVS = "obmc/hwmon/{0}"
# compose_list is not defined immediately so don't use :=
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:zaius = " ${@compose_list(d, 'ENVS', 'ZAIUS_ITEMS')}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:zaius = " ${@compose_list(d, 'ENVS', 'ZAIUS_OCCITEMS')}"
