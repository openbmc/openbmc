SUMMARY = "Phosphor Debug Collector"
DESCRIPTION = "Phosphor Debug Collector provides mechanisms \
to collect various FFDC files and system parameters. \
This will be helpful for troubleshooting the problems in OpenBMC \
based systems."

PR = "r1"

inherit autotools \
        pkgconfig \
        obmc-phosphor-dbus-service \
        pythonnative

require phosphor-debug-collector.inc

DEPENDS += " \
        sdbusplus \
        sdbusplus-native \
        phosphor-logging \
        phosphor-dbus-interfaces \
        phosphor-dbus-interfaces-native \
        autoconf-archive-native \
        "

RDEPENDS_${PN} += " \
        sdbusplus \
        phosphor-logging \
        phosphor-dbus-interfaces \
        "

DBUS_SERVICE_${PN} += "xyz.openbmc_project.Dump.service"

S = "${WORKDIR}/git"
