SUMMARY = "State Management DBUS Object"
DESCRIPTION = "State Management DBUS Object"
HOMEPAGE = "https://github.com/openbmc/phosphor-state-manager"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

DBUS_SERVICE_${PN} += "xyz.openbmc_project.State.Host.service"

RDEPENDS_${PN} += "libsystemd"
DEPENDS += "autoconf-archive-native"
DEPENDS += "systemd"
DEPENDS += "sdbusplus"

PROVIDES += "virtual/obmc-state-mgmt"
RPROVIDES_${PN} += "virtual-obmc-state-mgmt"

SRC_URI += "git://github.com/openbmc/phosphor-state-manager"
SRCREV = "1cb8b7070f44470953f61f7143b6717b8366d9c8"

S = "${WORKDIR}/git"
