SUMMARY = "Phosphor Fan Presence"
DESCRIPTION = "Phosphor fan presence provides a set of fan presence \
daemons to monitor fan presence changes by different methods of \
presence detection."
HOMEPAGE = "https://github.com/openbmc/phosphor-fan-presence"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig pythonnative
inherit obmc-phosphor-dbus-service

DEPENDS += "autoconf-archive-native"
DEPENDS += "python-pyyaml-native"
DEPENDS += "systemd"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"
RDEPENDS_${PN} += "libsystemd"
RDEPENDS_${PN} += "sdbusplus"

FILES_${PN} = "${sbindir}/phosphor-fan-presence-tach"
DBUS_SERVICE_${PN} += "xyz.openbmc_project.phosphor-fan-presence-tach.service"

SRC_URI += "git:///home/msbarth/openbmc/phosphor-fan-presence"
#SRC_URI += "git://github.com/openbmc/phosphor-fan-presence"
SRCREV = "c027aa6029e9193f26111c960a30e56f44012da8"

S = "${WORKDIR}/git"
