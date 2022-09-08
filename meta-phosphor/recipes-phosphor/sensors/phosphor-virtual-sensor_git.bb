SUMMARY = "Virtual Sensors"
DESCRIPTION = "virtual sensors created from existing sensors and config data"
HOMEPAGE = "https://github.com/openbmc/phosphor-virtual-sensor"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9e69ba356fa59848ffd865152a3ccc13"

inherit meson pkgconfig
inherit systemd

DEPENDS:append = "\
    exprtk \
    nlohmann-json \
    phosphor-dbus-interfaces \
    phosphor-logging \
    sdbusplus \
"

SRC_URI = "git://github.com/openbmc/phosphor-virtual-sensor.git;protocol=https;branch=master"
SRCREV = "dc7770153b8fdfca26f1e7fd038033dff6b20456"
S = "${WORKDIR}/git"

SYSTEMD_SERVICE:${PN} = "phosphor-virtual-sensor.service"
