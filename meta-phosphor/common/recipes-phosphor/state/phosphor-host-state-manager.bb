SUMMARY = "Phosphor Host State Management"
DESCRIPTION = "Phosphor Host State Manager provides a host state \
object which manages the hosts in the system. It is suitable for use on \
a wide variety of OpenBMC platforms."
HOMEPAGE = "https://github.com/openbmc/phosphor-state-manager"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

DBUS_SERVICE_${PN} += "xyz.openbmc_project.State.Host.service"

RDEPENDS_${PN} += "libsystemd"
DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"

PROVIDES += "virtual/obmc-host-state-mgmt"
RPROVIDES_${PN} += "virtual-obmc-host-state-mgmt"

SRC_URI += "git://github.com/openbmc/phosphor-state-manager"
SRCREV = "1cb8b7070f44470953f61f7143b6717b8366d9c8"

S = "${WORKDIR}/git"
