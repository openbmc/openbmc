SUMMARY = "Phosphor Debug Collector"
DESCRIPTION = "Phosphor Debug Collector provides mechanisms \
to collect various FFDC files and system parameters. \
This will be helpful for troubleshooting the problems in OpenBMC \
based systems."

PR = "r1"

DEBUG_COLLECTOR_PKGS = " \
    ${PN}-manager \
    ${PN}-monitor \
"
PACKAGES =+ "${DEBUG_COLLECTOR_PKGS}"
PACKAGES_remove = "${PN}"
RDEPENDS_${PN}-dev = "${DEBUG_COLLECTOR_PKGS}"
RDEPENDS_${PN}-staticdev = "${DEBUG_COLLECTOR_PKGS}"

DBUS_PACKAGES = "${PN}-manager"

SYSTEMD_PACKAGES = "${PN}-monitor"

inherit autotools \
        pkgconfig \
        obmc-phosphor-dbus-service \
        pythonnative

require phosphor-debug-collector.inc

DEPENDS += " \
        phosphor-dbus-interfaces \
        phosphor-dbus-interfaces-native \
        phosphor-logging \
        sdbusplus \
        sdbusplus-native \
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

DBUS_SERVICE_${PN}-manager += "xyz.openbmc_project.Dump.service"
SYSTEMD_SERVICE_${PN}-monitor += "obmc-dump-monitor.service"

S = "${WORKDIR}/git"
