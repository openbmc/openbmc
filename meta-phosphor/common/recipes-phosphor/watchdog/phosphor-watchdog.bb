SUMMARY = "Phosphor Watchdog application"
DESCRIPTION = "Application that implements software watchdog"
HOMEPAGE = "http://github.com/openbmc/phosphor-watchdog"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

RPROVIDES_${PN} += "virtual/obmc-watchdog"
PROVIDES += "virtual/obmc-watchdog"

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus sdbusplus-native"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "phosphor-logging"
RDEPENDS_${PN} += "libsystemd"
RDEPENDS_${PN} += "phosphor-logging"

# This is really a DBUS service but the service name is
# an argument, so making it this way.
SYSTEMD_SERVICE_${PN} += "phosphor-watchdog@.service"

SRC_URI += "git://github.com/openbmc/phosphor-watchdog"
SRCREV = "f2309dd978eacd53343b68e012a1406eaf484e2e"
S = "${WORKDIR}/git"
