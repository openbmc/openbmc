SUMMARY = "OpenBMC Buttons"
DESCRIPTION = "OpenBMC All buttons"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

S = "${WORKDIR}/git"
SRC_URI += "git://github.com/openbmc/phosphor-buttons.git"
SRCREV = "391a21a1bb002a62ce336bfacdd8fdf888b7909a"

inherit cmake pkgconfig systemd

SYSTEMD_SERVICE_${PN} += "xyz.openbmc_project.Chassis.Buttons.service"

DEPENDS += " \
    systemd \
    sdbusplus \
    phosphor-dbus-interfaces \
    phosphor-logging \
    nlohmann-json \
    gpioplus \
    "
