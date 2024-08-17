SUMMARY = "Phosphor OpenBMC SSIF to DBUS"
DESCRIPTION = "Phosphor OpenBMC SSIF to DBUS."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
DEPENDS += "systemd"
DEPENDS += "phosphor-logging"
DEPENDS += "sdbusplus"
DEPENDS += "cli11"
PROVIDES += "virtual/obmc-host-ipmi-hw"
SRCREV = "7ead0d3d83fa3b54c44c4a9f931defe626b5714b"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/ssifbridge.git;protocol=https;branch=master"

SYSTEMD_SERVICE:${PN} = "ssifbridge.service"
S = "${WORKDIR}/git"

inherit meson pkgconfig
inherit systemd

RRECOMMENDS:${PN} += "phosphor-ipmi-host"

RPROVIDES:${PN} += "virtual-obmc-host-ipmi-hw"
