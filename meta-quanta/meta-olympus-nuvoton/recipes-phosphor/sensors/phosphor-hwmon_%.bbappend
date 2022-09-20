FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " file://xyz.openbmc_project.Hwmon_hostoff@.service"
SRC_URI:append:olympus-nuvoton = " file://olympus-reload-sensor.sh"
#SRC_URI:append:olympus-nuvoton = " file://0001-lev-add-poweron-monitor-feature.patch"
SRC_URI:append:olympus-nuvoton = " file://olympus-reload-sensor-on.service"
SRC_URI:append:olympus-nuvoton = " file://olympus-reload-sensor-off.service"

SYSTEMD_SERVICE:${PN}:append:olympus-nuvoton = " olympus-reload-sensor-on.service"
SYSTEMD_SERVICE:${PN}:append:olympus-nuvoton = " olympus-reload-sensor-off.service"
SYSTEMD_SERVICE:${PN}:append:olympus-nuvoton = " xyz.openbmc_project.Hwmon_hostoff@.service"
SYSTEMD_SERVICE:${PN}:append:olympus-nuvoton = " olympus-reload-sensor-on.service"
SYSTEMD_SERVICE:${PN}:append:olympus-nuvoton = " olympus-reload-sensor-off.service"

ITEMS = " \
        i2c@82000/tmp421@4c \
        i2c@82000/power-supply@58 \
        i2c@86000/tps53679@60 \
        i2c@86000/tps53659@62 \
        i2c@86000/tps53659@64 \
        i2c@86000/tps53679@70 \
        i2c@86000/tps53659@72 \
        i2c@86000/tps53659@74 \
        i2c@86000/tps53622@67 \
        i2c@86000/tps53622@77 \
        i2c@86000/ina219@40 \
        i2c@86000/ina219@41 \
        i2c@86000/ina219@44 \
        i2c@86000/ina219@45 \
        i2c@87000/tmp421@4c \
        i2c@88000/adm1278@11 \
        i2c@8d000/tmp75@4a  \
        pwm-fan-controller@103000 \
        adc@c000 \
        "

ENVS = "obmc/hwmon/ahb/apb/{0}.conf"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:olympus-nuvoton = " ${@compose_list(d, 'ENVS', 'ITEMS')}"

# PECI
PECINAMES = " \
        peci-0/0-30/peci_cpu.cputemp.skx.48 \
        peci-0/0-31/peci_cpu.cputemp.skx.49\
        peci-0/0-30/peci_cpu.dimmtemp.skx.48 \
        peci-0/0-31/peci_cpu.dimmtemp.skx.49 \
        "
PECIITEMSFMT = "devices/platform/ahb/ahb--apb/ahb--apb--peci-bus@100000/f0100000.peci-bus/{0}.conf"
PECIITEMS = "${@compose_list(d, 'PECIITEMSFMT', 'PECINAMES')}"
PECIENVS = "obmc/hwmon/{0}"
SYSTEMD_ENVIRONMENT_FILE:${PN}:append:olympus-nuvoton = " ${@compose_list(d, 'PECIENVS', 'PECIITEMS')}"

EXTRA_OEMESON:append:olympus-nuvoton = " -Dupdate-functional-on-fail=true"

SENSOR_ON_TMPL = "olympus-reload-sensor-on.service"
CHASSIS_POWERON_TGTFMT = "obmc-chassis-poweron.target"
ENABLE_POWER_FMT = "../${SENSOR_ON_TMPL}:${CHASSIS_POWERON_TGTFMT}.wants/${SENSOR_ON_TMPL}"
SYSTEMD_LINK:${PN}:append:olympus-nuvoton = " ${@compose_list(d, 'ENABLE_POWER_FMT', 'OBMC_CHASSIS_INSTANCES')}"

SENSOR_OFF_TMPL = "olympus-reload-sensor-off.service"
CHASSIS_POWEROFF_TGTFMT = "obmc-chassis-poweroff.target"
DISABLE_POWER_FMT = "../${SENSOR_OFF_TMPL}:${CHASSIS_POWEROFF_TGTFMT}.wants/${SENSOR_OFF_TMPL}"
SYSTEMD_LINK:${PN}:append:olympus-nuvoton = " ${@compose_list(d, 'DISABLE_POWER_FMT', 'OBMC_CHASSIS_INSTANCES')}"


do_install:append:olympus-nuvoton() {
    install -d ${D}/${bindir}
    install -m 0755 ${WORKDIR}/olympus-reload-sensor.sh ${D}${bindir}/
}
