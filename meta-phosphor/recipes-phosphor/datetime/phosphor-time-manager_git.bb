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
RDEPENDS_${PN} += "phosphor-network"
RDEPENDS_${PN} += "phosphor-mapper"

SRC_URI += "git://github.com/openbmc/phosphor-time-manager"
SRCREV = "248a709c83e80f81a687c971b79ff292c4778fb0"
PV = "1.0+git${SRCPV}"
S = "${WORKDIR}/git"

DBUS_SERVICE_${PN} += "xyz.openbmc_project.Time.Manager.service"
