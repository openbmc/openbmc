SUMMARY = "Debug trigger"
DESCRIPTION = "Forcibly crash an unresponsive system to collect debug data"
HOMEPAGE = "https://github.com/openbmc/debug-trigger"

SRC_URI = "git://github.com/openbmc/debug-trigger;branch=master;protocol=https"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

S = "${WORKDIR}/git"

inherit meson
inherit pkgconfig
inherit systemd

PR = "r1"
PV = "0.1+git${SRCPV}"
SRCREV = "90550631988b41092812560a967d0ebb1cbd1fa8"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

DEBUG_TRIGGERS ?= ""

PACKAGECONFIG[systemd] = " \
    -Dsystemd=true, \
    -Dsystemd=false, \
    systemd"

PACKAGECONFIG[triggers] = " \
    -Dtriggers=${DEBUG_TRIGGERS}, \
    -Dtriggers=[], \
    systemd udev"

SYSTEMD_SERVICE:${PN} += "debug-trigger@.service"
