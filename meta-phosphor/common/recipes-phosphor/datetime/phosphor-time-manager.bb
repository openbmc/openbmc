SUMMARY = "Phosphor Time Manager daemon"
DESCRIPTION = "Daemon to cater to BMC and HOST time management"
HOMEPAGE = "http://github.com/openbmc/phosphor-time-manager"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
inherit autotools pkgconfig pythonnative
inherit obmc-phosphor-dbus-service

DEPENDS += "autoconf-archive-native"
DEPENDS += "phosphor-mapper"
DEPENDS += "systemd"
DEPENDS += "sdbusplus"
DEPENDS += "sdbusplus-native"
DEPENDS += "phosphor-logging"
DEPENDS += "phosphor-dbus-interfaces"
RDEPENDS_${PN} += "${VIRTUAL-RUNTIME_obmc-settings-mgmt}"
RDEPENDS_${PN} += "network"
RDEPENDS_${PN} += "libmapper"
RDEPENDS_${PN} += "libsystemd"
RDEPENDS_${PN} += "sdbusplus"
RDEPENDS_${PN} += "phosphor-dbus-interfaces"

SRC_URI += "git://github.com/openbmc/phosphor-time-manager"
SRCREV = "85c04d664dfaf5f7ecf7e76a9abd225b1e76d7e1"
S = "${WORKDIR}/git"

DBUS_SERVICE_${PN} += "xyz.openbmc_project.Time.Manager.service"
