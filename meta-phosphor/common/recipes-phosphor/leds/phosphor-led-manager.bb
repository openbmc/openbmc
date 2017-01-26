SUMMARY = "Phosphor LED Group Management Daemon"
DESCRIPTION = "Daemon to cater to triggering actions on LED groups"
HOMEPAGE = "http://github.com/openbmc/phosphor-led-manager"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
inherit pythonnative
inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

DEPENDS += "python-pyyaml-native"
DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus sdbusplus-native"
RDEPENDS_${PN} += "libsystemd"

PROVIDES += "virtual/obmc-leds-manager"
RPROVIDES_${PN} += "virtual-obmc-leds-manager"

SRC_URI += "git://github.com/openbmc/phosphor-led-manager"
SRCREV = "ed4907342897301ab96cbf2c617cafd78caf25a1"
S = "${WORKDIR}/git"

DBUS_SERVICE_${PN} += "xyz.openbmc_project.ledmanager.service"
