SUMMARY = "dbus-sensors"
DESCRIPTION = "Dbus Sensor Services Configured from D-Bus"

SRC_URI = "git://github.com/openbmc/dbus-sensors.git"
SRCREV = "ffa07e254fb36bf54c92ba3714e5fc4b6dbc2f8c"

PV = "0.1+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SYSTEMD_SERVICE_${PN} = "xyz.openbmc_project.fansensor.service"
SYSTEMD_SERVICE_${PN} += " xyz.openbmc_project.adcsensor.service"
SYSTEMD_SERVICE_${PN} += " xyz.openbmc_project.hwmontempsensor.service"
SYSTEMD_SERVICE_${PN} += " xyz.openbmc_project.cpusensor.service"
SYSTEMD_SERVICE_${PN} += " xyz.openbmc_project.exitairsensor.service"
SYSTEMD_SERVICE_${PN} += " xyz.openbmc_project.ipmbsensor.service"
SYSTEMD_SERVICE_${PN} += " xyz.openbmc_project.intrusionsensor.service"
SYSTEMD_SERVICE_${PN} += " xyz.openbmc_project.psusensor.service"
SYSTEMD_SERVICE_${PN} += " xyz.openbmc_project.mcutempsensor.service"

DEPENDS = "boost nlohmann-json sdbusplus i2c-tools libgpiod"
inherit cmake systemd

S = "${WORKDIR}/git/"

# linux-libc-headers guides this way to include custom uapi headers
CXXFLAGS_append = " -I ${STAGING_KERNEL_DIR}/include/uapi"
CXXFLAGS_append = " -I ${STAGING_KERNEL_DIR}/include"
do_configure[depends] += "virtual/kernel:do_shared_workdir"
EXTRA_OECMAKE = "-DYOCTO=1"
