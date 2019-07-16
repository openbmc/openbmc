FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"


NAMES = " \
        i2c@81000/lm75@5c \
        i2c@82000/lm75@5c \
        i2c@83000/lm75@5c \
        i2c@84000/lm75@5c  \
        i2c@8b000/hotswap@15 \
        i2c@8b000/power-brick@36 \
        i2c@8c000/ucd90160@6b \
        "
ITEMSFMT = "ahb/apb/{0}.conf"

ITEMS += "${@compose_list(d, 'ITEMSFMT', 'NAMES')}"

ENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_gsj = " ${@compose_list(d, 'ENVS', 'ITEMS')}"

# Fan sensors
FITEMS = "pwm-fan-controller@103000.conf"
FENVS = "obmc/hwmon/ahb/apb/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN}_append_gsj = " ${@compose_list(d, 'FENVS', 'FITEMS')}"
