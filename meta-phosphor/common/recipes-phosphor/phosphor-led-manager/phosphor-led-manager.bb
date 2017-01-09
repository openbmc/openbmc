SUMMARY = "Phosphor LED group manager"
DESCRIPTION = "Daemon to cater to triggering actions on LED groups"
HOMEPAGE = "http://github.com/openbmc/phosphor-led-manager"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
inherit pythonnative
inherit autotools pkgconfig
inherit obmc-phosphor-python-autotools

DEPENDS += "python-pyyaml-native"
DEPENDS += "python-pyyaml"
DEPENDS += "autoconf-archive-native"
DEPENDS += "systemd"
DEPENDS += "sdbusplus sdbusplus-native"
RDEPENDS_${PN} += "libsystemd"
PROVIDES += "virtual/obmc-led-group-mgmt"
RPROVIDES_${PN} += "virtual-obmc-led-group-mgmt"

SRC_URI += "git://github.com/openbmc/phosphor-led-manager"
SRCREV = "4c8c72bc9131a0ad6a9685678528d95afc7c6a9a"
S = "${WORKDIR}/git"

DBUS_SERVICE_${PN} += "xyz.openbmc_project.ledmanager.service"
