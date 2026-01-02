SUMMARY = "Chassis Power Control"
HOMEPAGE = "https://github.com/openbmc/phosphor-power-control"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
DEPENDS += "phosphor-logging"
DEPENDS += "nlohmann-json"
DEPENDS += "gpioplus"
SRCREV = "a820c58e0d3fbdb6ffc721bf40797b39d2f13f12"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-power-control;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig
