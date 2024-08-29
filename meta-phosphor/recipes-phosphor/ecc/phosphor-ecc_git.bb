SUMMARY = "Phosphor BMC Memory ECC Logging"
DESCRIPTION = "Daemon to monitor and report the BMC memory ECC"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-logging"
SRCREV = "28b153cbb9ab588b97f801d29f29d2d0ccbce47a"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-ecc.git;branch=master;protocol=https"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} = " ${PN}.service"

inherit meson pkgconfig
inherit systemd

RDEPENDS:${PN} += "phosphor-sel-logger"
