FILESEXTRAPATHS:prepend:gbs := "${THISDIR}/${PN}:"

DEPENDS += "boost"

GBS_NAMES = " \
        i2c@82000/sbtsi@4c \
        i2c@85000/i2c-switch@71/i2c@0/max31725@54 \
        i2c@85000/i2c-switch@71/i2c@1/max31725@55 \
        i2c@85000/i2c-switch@71/i2c@2/max31725@5d \
        i2c@88000/adm1272@10 \
        i2c@89000/i2c-switch@71/i2c@0/vrm@60 \
        i2c@89000/i2c-switch@71/i2c@1/vrm@61 \
        i2c@89000/i2c-switch@71/i2c@2/vrm@63 \
        i2c@89000/i2c-switch@71/i2c@3/vrm@45 \
        i2c@8c000/max34451@4e \
        i2c@8c000/vrm@5d \
        i2c@8c000/vrm@5e \
        "
GBS_ITEMSFMT = "ahb/apb/{0}.conf"

GBS_ITEMS += "${@compose_list(d, 'GBS_ITEMSFMT', 'GBS_NAMES')}"
GBS_ITEMS:append:gbs += " iio-hwmon-battery.conf"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:gbs = " ${@compose_list(d, 'ENVS', 'GBS_ITEMS')}"

# Fan sensors
FITEMS = "pwm-fan-controller@103000.conf"
FENVS = "obmc/hwmon/ahb/apb/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:gbs = " ${@compose_list(d, 'FENVS', 'FITEMS')}"


EXTRA_OEMESON:append:gbs = " -Dupdate-functional-on-fail=true -Dnegative-errno-on-fail=false"
