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

# This needs to be executed as part of host crash
DEBUG_TMPL = "openpower-debug-collector@.service"
CRASH_TGTFMT = "obmc-host-crash@{0}.target"
DEBUG_INSTFMT = "openpower-debug-collector@{0}.service"
CRASH_DEBUG_FMT = "../${DEBUG_TMPL}:${CRASH_TGTFMT}.wants/${DEBUG_INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${DEBUG_TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'CRASH_DEBUG_FMT', 'OBMC_HOST_INSTANCES')}"
