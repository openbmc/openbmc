SUMMARY = "Phosphor Physical LED Controller daemon"
DESCRIPTION = "Daemon to trigger actions on a physical LED"
HOMEPAGE = "http://github.com/openbmc/phosphor-led-sysfs"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit meson pkgconfig
inherit obmc-phosphor-dbus-service

DEPENDS += "sdbusplus"
DEPENDS += "systemd"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "boost"

DBUS_SERVICE:${PN} += "xyz.openbmc_project.led.controller@.service"

EXTRA_OEMESON:append = " -Dtests=disabled"

SRC_URI += "git://github.com/openbmc/phosphor-led-sysfs"
SRC_URI += "file://70-leds.rules"
SRCREV = "520c5e51fd5df42e930c14ccf7dcc3c44d829a87"
S = "${WORKDIR}/git"

do_install:append() {
        install -d ${D}/${nonarch_base_libdir}/udev/rules.d/
        install -m 0644 ${WORKDIR}/70-leds.rules ${D}/${nonarch_base_libdir}/udev/rules.d/
}
