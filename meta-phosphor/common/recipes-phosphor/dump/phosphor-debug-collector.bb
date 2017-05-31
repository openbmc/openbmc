SUMMARY = "Phosphor Debug Collector"
DESCRIPTION = "Phosphor Debug Collector provides mechanisms \
to collect various FFDC files and system parameters. \
This will be helpful for troubleshooting the problems in OpenBMC \
based systems."

PR = "r1"

DUMP_MGR_PACKAGES = " \
    ${PN}-manager \
    ${PN}-monitor \
"
PACKAGES =+ "${DUMP_MGR_PACKAGES}"
PACKAGES_remove = "${PN}"
RDEPENDS_${PN}-dev = "${DUMP_MGR_PACKAGES}"
RDEPENDS_${PN}-staticdev = "${DUMP_MGR_PACKAGES}"

DBUS_PACKAGES = "${DUMP_MGR_PACKAGES}"

# Set SYSTEMD_PACKAGES to empty because we do not want ${PN} and DBUS_PACKAGES
# handles the rest.
SYSTEMD_PACKAGES = ""

inherit autotools \
        pkgconfig \
        obmc-phosphor-dbus-service

require phosphor-debug-collector.inc

DEPENDS += " \
        phosphor-dbus-interfaces \
        phosphor-logging \
        sdbusplus \
        autoconf-archive-native \
"

RDEPENDS_${PN}-manager += " \
        sdbusplus \
        phosphor-dbus-interfaces \
        phosphor-logging \
"
RDEPENDS_${PN}-monitor += " \
        sdbusplus \
        phosphor-dbus-interfaces \
        phosphor-logging \
"

FILES_${PN}-manager += "${sbindir}/phosphor-dump-manager"
FILES_${PN}-monitor += "${sbindir}/phosphor-dump-monitor"

DBUS_SERVICE_${PN}-manager += "xyz.openbmc_project.Dump.Manager.service"
DBUS_SERVICE_${PN}-monitor += "xyz.openbmc_project.Dump.Monitor.service"

S = "${WORKDIR}/git"
