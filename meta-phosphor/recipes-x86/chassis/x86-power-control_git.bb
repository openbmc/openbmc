SUMMARY = "Chassis Power Control service for Intel based platforms"
DESCRIPTION = "Chassis Power Control service for Intel based platforms"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
SRCREV = "fa2b6448a5169b5e981a24ae4ca0c03b80a07149"
PV = "1.0+git${SRCPV}"

SRC_URI = "git://github.com/openbmc/x86-power-control.git;protocol=https;branch=master"

S = "${WORKDIR}/git"

inherit meson systemd pkgconfig
inherit obmc-phosphor-dbus-service

def get_service(d):
    if(d.getVar('OBMC_HOST_INSTANCES') == '0'):
      return "xyz.openbmc_project.Chassis.Control.Power@0.service"
    else:
      return " ".join(["xyz.openbmc_project.Chassis.Control.Power@{}.service".format(x) for x in d.getVar('OBMC_HOST_INSTANCES').split()])
SYSTEMD_SERVICE:${PN} = "${@get_service(d)}"
SYSTEMD_SERVICE:${PN} += "chassis-system-reset.service \
                         chassis-system-reset.target"
DEPENDS += " \
    boost \
    i2c-tools \
    libgpiod \
    nlohmann-json \
    sdbusplus \
    phosphor-logging \
  "
FILES:${PN}  += "${systemd_system_unitdir}/xyz.openbmc_project.Chassis.Control.Power@.service"
