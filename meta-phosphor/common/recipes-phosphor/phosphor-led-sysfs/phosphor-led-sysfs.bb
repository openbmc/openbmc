SUMMARY = "Phosphor Physical LED Controller daemon"
DESCRIPTION = "Daemon to trigger actions on a physical LED"
HOMEPAGE = "http://github.com/openbmc/phosphor-led-sysfs"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
inherit autotools pkgconfig

DEPENDS += "autoconf-archive-native"
DEPENDS += "systemd"
DEPENDS += "sdbusplus sdbusplus-native"
RDEPENDS_${PN} += "libsystemd"
PROVIDES += "virtual/obmc-control-physical-led"
RPROVIDES_${PN} += "virtual-obmc-control-physical-led"

SRC_URI += "git://github.com/openbmc/phosphor-led-sysfs"
SRCREV = "61675c3265166cecb0f8f5c2c4f80a78d3412c25"

S = "${WORKDIR}/git"
