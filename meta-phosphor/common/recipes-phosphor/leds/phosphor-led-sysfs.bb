SUMMARY = "Phosphor Physical LED Controller daemon"
DESCRIPTION = "Daemon to trigger actions on a physical LED"
HOMEPAGE = "http://github.com/openbmc/phosphor-led-sysfs"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-dbus-interfaces"
RDEPENDS_${PN} += "libsystemd"

DBUS_SERVICE_${PN} += "xyz.openbmc_project.led.controller@.service"

SRC_URI += "git://github.com/openbmc/phosphor-led-sysfs"
SRC_URI += "file://70-leds.rules"
SRCREV = "f9de54be38be17456dae06408ed93f3416a6621f"
S = "${WORKDIR}/git"

do_install_append() {
        install -d ${D}/${base_libdir}/udev/rules.d/
        install ${WORKDIR}/70-leds.rules ${D}/${base_libdir}/udev/rules.d/
}
