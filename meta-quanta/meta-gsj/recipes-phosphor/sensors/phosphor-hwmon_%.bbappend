FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"


NAMES = " \
        i2c-bus@81000/lm75@5c \
        i2c-bus@82000/lm75@5c \
        i2c-bus@83000/lm75@5c \
        i2c-bus@84000/lm75@5c  \
        i2c-bus@8b000/hotswap@15 \
        i2c-bus@8b000/power-brick@36 \
        "
ITEMSFMT = "ahb/apb/{0}.conf"

ITEMS += "${@compose_list(d, 'ITEMSFMT', 'NAMES')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'ITEMS')}"

# Fan sensors
FITEMS = "pwm-fan-controller@103000.conf"
FENVS = "obmc/hwmon/ahb/apb/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'FENVS', 'FITEMS')}"
