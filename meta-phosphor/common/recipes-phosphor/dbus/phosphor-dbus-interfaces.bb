SUMMARY = "Phosphor DBus Interfaces"
DESCRIPTION = "Generated bindings, using sdbus++, for the phosphor YAML"
PR = "r1"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
inherit obmc-phosphor-license
inherit pythonnative

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus sdbus++-native"

RDEPENDS_${PN} += "libsystemd"

SRC_URI += "git://github.com/openbmc/phosphor-dbus-interfaces"
SRCREV = "5d4f2379695f7c28549b107f325e0b0cd43d83b1"
