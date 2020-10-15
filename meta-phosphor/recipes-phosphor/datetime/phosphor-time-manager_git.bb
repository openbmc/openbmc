SUMMARY = "Phosphor Time Manager daemon"
DESCRIPTION = "Daemon to cater to BMC and HOST time management"
HOMEPAGE = "http://github.com/openbmc/phosphor-time-manager"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
inherit autotools pkgconfig python3native
inherit obmc-phosphor-dbus-service

DEPENDS += "autoconf-archive-native"
DEPENDS += "phosphor-mapper"
DEPENDS += "systemd"
DEPENDS += "sdbusplus"
DEPENDS += "${PYTHON_PN}-sdbus++-native"
DEPENDS += "phosphor-logging"
DEPENDS += "phosphor-dbus-interfaces"
RDEPENDS_${PN} += "phosphor-settings-manager"
RDEPENDS_${PN} += "phosphor-network"
RDEPENDS_${PN} += "phosphor-mapper"
RDEPENDS_${PN} += "bash"

FILES_${PN}+= "${bindir}/hosttimesync/SetTimeBmc"
FILES_${PN}+= "{bindir}/hosttimesync/time-manager-bmc-set-time"
SYSTEMD_SERVICE_${PN}+= "time-manager-bmc-set-time.service"

SRC_URI += "git://github.com/openbmc/phosphor-time-manager"
SRCREV = "3de9698dae2d251cba482d4ccc78d58d2a02d564"
PV = "1.0+git${SRCPV}"
S = "${WORKDIR}/git"

DBUS_SERVICE_${PN} += "xyz.openbmc_project.Time.Manager.service"

