SUMMARY = "Phosphor Time Manager daemon"
DESCRIPTION = "Daemon to cater to BMC and HOST time management"
HOMEPAGE = "http://github.com/openbmc/phosphor-time-manager"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
inherit autotools pkgconfig

DEPENDS += "autoconf-archive-native"
DEPENDS += "phosphor-mapper"
DEPENDS += "systemd"
RDEPENDS_${PN} += "phosphor-settings"
RDEPENDS_${PN} += "network"
RDEPENDS_${PN} += "libmapper"
RDEPENDS_${PN} += "libsystemd"
SRC_URI += "git://github.com/openbmc/phosphor-time-manager"

SRCREV = "2a96cfc0ed4e5466793a82292ee5640c992b963c"

S = "${WORKDIR}/git"

DBUS_SERVICE_${PN} += "org.openbmc.TimeManager.service"
