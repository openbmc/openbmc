FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://rsyslog.conf \
"

PACKAGECONFIG ??= " \
    rsyslogd rsyslogrt inet regexp uuid \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
"
