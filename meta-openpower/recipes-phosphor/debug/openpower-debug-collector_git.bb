SUMMARY = "OpenPOWER Debug Collector"
DESCRIPTION = "Application to log error during host checkstop and watchdog timeout"

PR = "r1"
PV = "1.0+git${SRCPV}"

inherit meson \
        obmc-phosphor-systemd \
        python3native \
        phosphor-dbus-yaml

require ${BPN}.inc
require ${BPN}-systemd-links.inc

DEPENDS += " \
        phosphor-logging \
        ${PYTHON_PN}-sdbus++-native \
        "
S = "${WORKDIR}/git"

# This provides below 2 applications that are called into in case
# of host checkstop and host watchdog timeout respectively.
APPS = "checkstop watchdog"

DEBUG_TMPL = "openpower-debug-collector-{0}@.service"
SYSTEMD_SERVICE_${PN} += "${@compose_list(d, 'DEBUG_TMPL', 'APPS')}"


# Do not depend on phosphor-logging for native build
DEPENDS_remove_class-native = "phosphor-logging"

# Do not depend on phosphor-logging for native SDK build
DEPENDS_remove_class-nativesdk = "phosphor-logging"

BBCLASSEXTEND += "native nativesdk"
