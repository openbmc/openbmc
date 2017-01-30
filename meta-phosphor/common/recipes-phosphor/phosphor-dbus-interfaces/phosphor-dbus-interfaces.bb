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
SRCREV = "3b34cd1d919f7eb0974b0370a4c667cec3f972ba"

SRC_URI += "file://0001-build-Generate-package-config-file.patch"
