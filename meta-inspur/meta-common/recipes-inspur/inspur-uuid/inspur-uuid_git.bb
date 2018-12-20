SUMMARY = "Inspur Identify LED Controller daemon"
DESCRIPTION = "Daemon to trigger actions on a inspur identify LED"

PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig
inherit systemd

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"
DEPENDS += "sdeventplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "phosphor-logging"
DEPENDS += "gpioplus"


SYSTEMD_SERVICE_${PN} += "xyz.openbmc_project.inspur.identify_led.controller.service"

SRC_URI = "git://github.com/inspur-bmc/inspur-uuid.git"

PV = "1.0+git${SRCPV}"
SRCREV = "703633f2e81ba4d59eef74910c019e91d2bac63c"

S = "${WORKDIR}/git"
