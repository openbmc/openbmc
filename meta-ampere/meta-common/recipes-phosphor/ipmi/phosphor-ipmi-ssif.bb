SUMMARY = "Phosphor OpenBMC SSIF to DBUS"
DESCRIPTION = "Phosphor OpenBMC SSIF to DBUS."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit meson pkgconfig
inherit systemd

SYSTEMD_SERVICE_${PN} = "ssifbridge.service"

PROVIDES += "virtual/obmc-host-ipmi-hw"
RPROVIDES_${PN} += "virtual-obmc-host-ipmi-hw"
RRECOMMENDS_${PN} += "phosphor-ipmi-host"

DEPENDS += "systemd"
DEPENDS += "phosphor-logging"
DEPENDS += "sdbusplus"
DEPENDS += "cli11"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/ampere-openbmc/ssifbridge;protocol=git;branch=main"
SRCREV= "8e690ce94e2d219b1710fc6890c255df3ab130b6"

