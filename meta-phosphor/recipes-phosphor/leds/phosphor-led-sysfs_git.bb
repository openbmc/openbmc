SUMMARY = "Phosphor Physical LED Controller daemon"
DESCRIPTION = "Daemon to trigger actions on a physical LED"
HOMEPAGE = "http://github.com/openbmc/phosphor-led-sysfs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS += "sdbusplus"
DEPENDS += "systemd"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "boost"
SRCREV = "02f366d68bb9752e6de04db0efc8eb885b19b219"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI += "git://github.com/openbmc/phosphor-led-sysfs;branch=master;protocol=https"
SRC_URI += "file://70-leds.rules"

S = "${WORKDIR}/git"

inherit meson pkgconfig
inherit obmc-phosphor-dbus-service

EXTRA_OEMESON:append = " -Dtests=disabled"

do_install:append() {
        install -d ${D}/${nonarch_base_libdir}/udev/rules.d/
        install -m 0644 ${WORKDIR}/70-leds.rules ${D}/${nonarch_base_libdir}/udev/rules.d/
}

DBUS_SERVICE:${PN} += "xyz.openbmc_project.led.controller@.service"
