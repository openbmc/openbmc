SUMMARY = "OpenPOWER Debug Collector"
DESCRIPTION = "Application to log error during host checkstop and watchdog timeout"

PR = "r1"
PV = "1.0+git${SRCPV}"

inherit pkgconfig meson \
        obmc-phosphor-systemd \
        python3native \
        phosphor-dbus-yaml

require ${BPN}.inc
require ${BPN}-systemd-links.inc

DEPENDS += " \
        phosphor-logging \
        ${PYTHON_PN}-sdbus++-native \
        cli11 \
        ipl \
        "
S = "${WORKDIR}/git"

# This provides below applications that are called into in case
# of host checkstop, host watchdog and host watchdog-timeout respectively.
APPS =  " \
        checkstop \
        watchdog \
        ${@bb.utils.contains('MACHINE_FEATURES', 'phal', '', 'watchdog-timeout', d)} \
        "

DEBUG_TMPL = "openpower-debug-collector-{0}@.service"
SYSTEMD_SERVICE:${PN} += "${@compose_list(d, 'DEBUG_TMPL', 'APPS')}"

# Do not depend on phosphor-logging for native build
DEPENDS:remove:class-native = "phosphor-logging"

# Do not depend on phosphor-logging for native SDK build
DEPENDS:remove:class-nativesdk = "phosphor-logging ipl"
