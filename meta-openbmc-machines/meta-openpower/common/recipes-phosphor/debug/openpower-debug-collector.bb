SUMMARY = "OpenPOWER Debug Collector"
DESCRIPTION = "Application to log error during host checkstop"

PR = "r1"

inherit autotools \
        pkgconfig \
        obmc-phosphor-systemd \
        pythonnative

require ${PN}.inc

DEPENDS += " \
        phosphor-logging \
        autoconf-archive-native \
        sdbus++-native \
        "
RDEPENDS_${PN} += " \
            phosphor-logging \
            "

S = "${WORKDIR}/git"

# This provides below 2 applications that are called into in case
# of host checkstop and host watchdog timeout respectively.
APPS = "checkstop watchdog"

DEBUG_TMPL = "openpower-debug-collector-{0}@.service"
DEBUG_INSTFMT = "openpower-debug-collector-{0}@{1}.service"
LINK_FMT = "${DEBUG_TMPL}:${DEBUG_INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'DEBUG_TMPL', 'APPS')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'LINK_FMT', 'APPS', 'OBMC_HOST_INSTANCES')}"
