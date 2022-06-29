SUMMARY = "Chassis Power Control"
HOMEPAGE = "https://github.com/openbmc/phosphor-power-control"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit meson pkgconfig

DEPENDS += "phosphor-logging"
DEPENDS += "nlohmann-json"
DEPENDS += "gpioplus"

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/openbmc/phosphor-power-control;branch=master;protocol=https"
SRCREV = "bebabec9cb3574f276d2a9885287f26706eab217"
