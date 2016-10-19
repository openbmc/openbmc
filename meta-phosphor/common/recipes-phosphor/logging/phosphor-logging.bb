SUMMARY = "Logging DBUS Object"
DESCRIPTION = "Logging DBUS Object"
HOMEPAGE = "https://github.com/openbmc/phosphor-logging"
PR = "r1"

inherit autotools pkgconfig
inherit obmc-phosphor-license

DEPENDS += "autoconf-archive-native"
PROVIDES += "virtual/obmc-logging-mgmt"
RPROVIDES_${PN} += "virtual-obmc-logging-mgmt"

SRC_URI += "git://github.com/openbmc/phosphor-logging"
SRCREV = "91da45310b8ba4d244ec0d9036c9e104d1b9f7c4"

S = "${WORKDIR}/git"

