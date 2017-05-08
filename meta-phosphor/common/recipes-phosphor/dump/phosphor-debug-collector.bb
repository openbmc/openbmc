SUMMARY = "Dump DBUS Object"
DESCRIPTION = "Dump DBUS Object"
PR = "r1"

inherit autotools \
        pkgconfig \
        obmc-phosphor-dbus-service

require phosphor-debug-collector.inc

DEPENDS += " \
        phosphor-dbus-interfaces \
        phosphor-dbus-interfaces-native \
        phosphor-logging \
        sdbusplus \
        sdbusplus-native \
        autoconf-archive-native \
        "

RDEPENDS_${PN} += "sdbusplus phosphor-dbus-interfaces"

DBUS_SERVICE_${PN} += "xyz.openbmc_project.Dump.service"

S = "${WORKDIR}/git"
