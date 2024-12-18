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
SRCREV = "d825f567fa94dd2cbd35bff761f1a5fbde34a4f3"
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
