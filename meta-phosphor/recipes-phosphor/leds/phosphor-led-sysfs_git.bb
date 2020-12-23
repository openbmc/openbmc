SUMMARY = "Phosphor Physical LED Controller daemon"
DESCRIPTION = "Daemon to trigger actions on a physical LED"
HOMEPAGE = "http://github.com/openbmc/phosphor-led-sysfs"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"
DEPENDS += "systemd"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "boost"

DBUS_SERVICE_${PN} += "xyz.openbmc_project.led.controller@.service"

SRC_URI += "git://github.com/openbmc/phosphor-led-sysfs"
SRC_URI += "file://70-leds.rules"
SRCREV = "61b906367119b794a9788725fad86a3174bd83c0"
S = "${WORKDIR}/git"

do_install_append() {
        install -d ${D}/${base_libdir}/udev/rules.d/
        install -m 0644 ${WORKDIR}/70-leds.rules ${D}/${base_libdir}/udev/rules.d/
}
