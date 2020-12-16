SUMMARY = "dbus-sensors"
DESCRIPTION = "Dbus Sensor Services Configured from D-Bus"

SRC_URI = "git://github.com/openbmc/dbus-sensors.git"
SRCREV = "937eb54e2fbdd917442bf359c8367219c23d4e36"

PV = "0.1+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

PACKAGECONFIG ??= " \
    adcsensor \
    cpusensor \
    exitairtempsensor \
    fansensor \
    hwmontempsensor \
    intrusionsensor \
    ipmbsensor \
    mcutempsensor \
    psusensor \
    "

PACKAGECONFIG[adcsensor] = "-DDISABLE_ADC=OFF, -DDISABLE_ADC=ON"
PACKAGECONFIG[cpusensor] = "-DDISABLE_CPU=OFF, -DDISABLE_CPU=ON"
PACKAGECONFIG[exitairtempsensor] = "-DDISABLE_EXIT_AIR=OFF, -DDISABLE_EXIT_AIR=ON"
PACKAGECONFIG[fansensor] = "-DDISABLE_FAN=OFF, -DDISABLE_FAN=ON"
PACKAGECONFIG[hwmontempsensor] = "-DDISABLE_HWMON_TEMP=OFF, -DDISABLE_HWMON_TEMP=ON"
PACKAGECONFIG[intrusionsensor] = "-DDISABLE_INTRUSION=OFF, -DDISABLE_INTRUSION=ON"
PACKAGECONFIG[ipmbsensor] = "-DDISABLE_IPMB=OFF, -DDISABLE_IPMB=ON"
PACKAGECONFIG[mcutempsensor] = "-DDISABLE_MCUTEMP=OFF, -DDISABLE_MCUTEMP=ON"
PACKAGECONFIG[psusensor] = "-DDISABLE_PSU=OFF, -DDISABLE_PSU=ON"

SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'adcsensor', \
                                               'xyz.openbmc_project.adcsensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'cpusensor', \
                                               'xyz.openbmc_project.cpusensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'exitairtempsensor', \
                                               'xyz.openbmc_project.exitairsensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'fansensor', \
                                               'xyz.openbmc_project.fansensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'hwmontempsensor', \
                                               'xyz.openbmc_project.hwmontempsensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'intrusionsensor', \
                                               'xyz.openbmc_project.intrusionsensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'ipmbsensor', \
                                               'xyz.openbmc_project.ipmbsensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'mcutempsensor', \
                                               'xyz.openbmc_project.mcutempsensor.service', \
                                               '', d)}"
SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'psusensor', \
                                               'xyz.openbmc_project.psusensor.service', \
                                               '', d)}"

DEPENDS = "boost nlohmann-json sdbusplus i2c-tools libgpiod"
inherit cmake systemd

S = "${WORKDIR}/git"

EXTRA_OECMAKE = "-DYOCTO=1"
