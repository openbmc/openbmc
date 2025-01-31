SUMMARY = "Phosphor Physical LED Controller daemon"
DESCRIPTION = "Daemon to trigger actions on a physical LED"
HOMEPAGE = "http://github.com/openbmc/phosphor-led-sysfs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS += "cli11"
DEPENDS += "sdbusplus"
DEPENDS += "systemd"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "boost"
DEPENDS += "phosphor-logging"
SRCREV = "b851ddfb464efaa52168c9c8037d4eb1eb78266c"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-led-sysfs;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig
inherit obmc-phosphor-dbus-service

EXTRA_OEMESON:append = " -Dtests=disabled"

SYSTEMD_SERVICE:${PN} += "phosphor-ledcontroller.service"

FILES:${PN} += "/usr/lib/systemd/system/sysfs-led@.service"
FILES:${PN} += "/usr/share/dbus-1/system-services/xyz.openbmc_project.LED.Controller.service"
