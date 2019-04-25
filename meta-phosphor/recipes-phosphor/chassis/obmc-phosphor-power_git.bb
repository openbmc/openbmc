SUMMARY = "Chassis Power Control"
HOMEPAGE = "https://github.com/openbmc/phosphor-power-control"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit autotools pkgconfig

DEPENDS += "autoconf-archive-native"
DEPENDS += "phosphor-logging"
DEPENDS += "nlohmann-json"
DEPENDS += "gpioplus"

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/openbmc/phosphor-power-control"
SRCREV = "4d209a24588d112dfd61158b2e01dd973d99961e"
