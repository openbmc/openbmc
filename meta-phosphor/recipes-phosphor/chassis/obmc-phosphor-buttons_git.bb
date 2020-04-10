SUMMARY = "OpenBMC Buttons"
DESCRIPTION = "OpenBMC All buttons"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

S = "${WORKDIR}/git"
SRC_URI += "git://github.com/openbmc/phosphor-buttons.git"
SRCREV = "582b3f025a0ca424d07aee67185d51915bdf2f46"

inherit cmake pkgconfig systemd

BUTTON_PACKAGES="${PN}-signals ${PN}-handler"

ALLOW_EMPTY_${PN} = "1"
PACKAGE_BEFORE_PN += "${BUTTON_PACKAGES}"
PACKAGECONFIG ??= "signals handler"
SYSTEMD_PACKAGES = "${BUTTON_PACKAGES}"

PACKAGECONFIG[signals] = ",,gpioplus nlohmann-json,"
PACKAGECONFIG[handler] = ",,,phosphor-state-manager-chassis phosphor-state-manager-host"

FILES_${PN}-signals = "${bindir}/buttons"
SYSTEMD_SERVICE_${PN}-signals = "xyz.openbmc_project.Chassis.Buttons.service"

FILES_${PN}-handler = "${bindir}/button-handler"
SYSTEMD_SERVICE_${PN}-handler = "phosphor-button-handler.service"

DEPENDS += " \
    systemd \
    sdbusplus \
    phosphor-dbus-interfaces \
    phosphor-logging \
    "
