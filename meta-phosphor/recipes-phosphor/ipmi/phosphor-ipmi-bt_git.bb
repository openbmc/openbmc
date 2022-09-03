SUMMARY = "Phosphor OpenBMC BT to DBUS"
DESCRIPTION = "Phosphor OpenBMC BT to DBUS."
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

DBUS_SERVICE:${PN} = "org.openbmc.HostIpmi.service"

PROVIDES += "virtual/obmc-host-ipmi-hw"
RPROVIDES:${PN} += "virtual-obmc-host-ipmi-hw"
RRECOMMENDS:${PN} += "phosphor-ipmi-host"

DEPENDS += "autoconf-archive-native"
DEPENDS += "systemd"

S = "${WORKDIR}/git"
SRC_URI += "git://github.com/openbmc/btbridge;branch=master;protocol=https"
SRCREV="a6ec899618a28a30f7907a6cc90fe027047297d1"
