SUMMARY = "Phosphor GPIO monitor application"
DESCRIPTION = "Application to monitor gpio assertions"
HOMEPAGE = "http://github.com/openbmc/phosphor-gpio-monitor"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

RPROVIDES_${PN} += "virtual/obmc-gpio-monitor"
PROVIDES += "virtual/obmc-gpio-monitor"

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus sdbusplus-native"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "libevdev"
DEPENDS += "phosphor-logging"
RDEPENDS_${PN} += "libsystemd"
RDEPENDS_${PN} += "libevdev"
RDEPENDS_${PN} += "phosphor-logging"

SYSTEMD_SERVICE_${PN} += "phosphor-gpio-monitor@.service"

SRC_URI += "git://github.com/openbmc/phosphor-gpio-monitor"
SRCREV = "ba8de42135766deab2125a784a89d3e51bc2e194"
S = "${WORKDIR}/git"
