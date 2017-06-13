SUMMARY = "OpenPOWER Debug Collector"
DESCRIPTION = "Application to log error during host checkstop and watchdog timeout"

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
SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'DEBUG_TMPL', 'APPS')}"

# This needs to be executed as part of host crash
CHECKSTOP_TMPL = "openpower-debug-collector-checkstop@.service"
CRASH_TGTFMT = "obmc-host-crash@{0}.target"
CHECKSTOP_INSTFMT = "openpower-debug-collector-checkstop@{0}.service"
CRASH_CHECKSTOP_FMT = "../${CHECKSTOP_TMPL}:${CRASH_TGTFMT}.wants/${CHECKSTOP_INSTFMT}"

# Make watchdog part of obmc-host-timeout target
WDOG_TMPL = "openpower-debug-collector-watchdog@.service"
TIMEOUT_TGTFMT = "obmc-host-timeout@{0}.target"
WDOG_INSTFMT = "openpower-debug-collector-watchdog@{0}.service"
TIMEOUT_WDOG_FMT = "../${WDOG_TMPL}:${TIMEOUT_TGTFMT}.wants/${WDOG_INSTFMT}"

SYSTEMD_LINK_${PN} += "${@compose_list(d, 'CRASH_CHECKSTOP_FMT', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'TIMEOUT_WDOG_FMT', 'OBMC_HOST_INSTANCES')}"
