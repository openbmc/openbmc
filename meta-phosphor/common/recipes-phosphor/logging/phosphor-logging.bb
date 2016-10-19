SUMMARY = "Logging DBUS Object"
DESCRIPTION = "Logging DBUS Object"
HOMEPAGE = "https://github.com/openbmc/phosphor-logging"
PR = "r1"

inherit autotools pkgconfig
inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

DBUS_SERVICE_${PN} += "xyz.openbmc_project.Logging.service"

DEPENDS += "autoconf-archive-native"
PROVIDES += "virtual/obmc-logging-mgmt"
RPROVIDES_${PN} += "virtual-obmc-logging-mgmt"

SRC_URI += "git://github.com/openbmc/phosphor-logging"
SRCREV = "1db1bd35557ba01323cbda710b4fc7ceb7dd9a13"

S = "${WORKDIR}/git"

