SUMMARY = "OpenPOWER OCC controller"
DESCRIPTION = "Application to contol the OpenPOWER On-Chip-Controller"
HOMEPAGE = "https://github.com/openbmc/openpower-occ-control"
PR = "r1"

inherit autotools \
        pkgconfig \
        obmc-phosphor-dbus-service \
        pythonnative

require ${PN}.inc

DBUS_SERVICE_${PN} += "org.open_power.OCC.Control.service"

DEPENDS += " \
        sdbusplus \
        sdbusplus-native \
        phosphor-logging \
        openpower-dbus-interfaces \
        phosphor-dbus-interfaces \
        openpower-dbus-interfaces-native \
        autoconf-archive-native \
        "

RDEPENDS_${PN} += " \
               sdbusplus \
               phosphor-logging \
               openpower-dbus-interfaces \
               phosphor-dbus-interfaces \
               "

S = "${WORKDIR}/git"
