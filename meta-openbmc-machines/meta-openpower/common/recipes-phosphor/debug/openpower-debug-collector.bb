SUMMARY = "OpenPOWER Debug Collector"
DESCRIPTION = "Application to log error during host checkstop"

PR = "r1"

inherit autotools \
        pkgconfig \
        obmc-phosphor-systemd \

require ${PN}.inc

DEPENDS += " \
        phosphor-logging \
        autoconf-archive-native \
        "

RDEPENDS_${PN} += " \
            phosphor-logging \
            "

S = "${WORKDIR}/git"

TMPL = "openpower-debug-collector@.service"
INSTFMT = "openpower-debug-collector@{0}.service"
LINK_FMT = "${TMPL}:${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'LINK_FMT', 'OBMC_CHECKSTOP_INSTANCES')}"
