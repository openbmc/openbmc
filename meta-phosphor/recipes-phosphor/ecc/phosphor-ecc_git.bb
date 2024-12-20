SUMMARY = "Phosphor BMC Memory ECC Logging"
DESCRIPTION = "Daemon to monitor and report the BMC memory ECC"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-logging"
SRCREV = "045aceaeaf80656e5a5fd85067ae04935f3bcf6c"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-ecc.git;branch=master;protocol=https"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} = " ${PN}.service"

inherit meson pkgconfig
inherit systemd

RDEPENDS:${PN} += "phosphor-sel-logger"
