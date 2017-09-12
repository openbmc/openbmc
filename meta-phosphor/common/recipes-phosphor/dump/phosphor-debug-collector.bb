SUMMARY = "Phosphor Debug Collector"
DESCRIPTION = "Phosphor Debug Collector provides mechanisms \
to collect various log files and system parameters. \
This will be helpful for troubleshooting the problems in OpenBMC \
based systems."

PR = "r1"

DEBUG_COLLECTOR_PKGS = " \
    ${PN}-manager \
    ${PN}-monitor \
    ${PN}-dreport \
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
        pythonnative \
        phosphor-debug-collector

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
        ${PN}-dreport \
"
RDEPENDS_${PN}-monitor += " \
        sdbusplus \
        phosphor-dbus-interfaces \
        phosphor-logging \
"
RDEPENDS_${PN}-dreport += " \
        systemd \
        ${VIRTUAL-RUNTIME_base-utils} \
        bash \
        xz \
"

MGR_SVC ?= "xyz.openbmc_project.Dump.Manager.service"

SYSTEMD_SUBSTITUTIONS += "BMC_DUMP_PATH:${bmc_dump_path}:${MGR_SVC}"

FILES_${PN}-manager += "${sbindir}/phosphor-dump-manager"
FILES_${PN}-monitor += "${sbindir}/phosphor-dump-monitor"
FILES_${PN}-dreport += "${bindir}/dreport"

DBUS_SERVICE_${PN}-manager += "${MGR_SVC}"
SYSTEMD_SERVICE_${PN}-monitor += " \
                          obmc-dump-monitor.service \
                          var-lib-systemd-coredump.mount \
"

EXTRA_OECONF = "BMC_DUMP_PATH=${bmc_dump_path}"

S = "${WORKDIR}/git"

do_install_append() {
       install -d ${D}${bindir}
       install -m 0755 ${S}/tools/dreport \
                       ${D}${bindir}/dreport
}
