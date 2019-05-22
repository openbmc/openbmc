FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://rsyslog.conf \
    file://server.conf \
"

PACKAGECONFIG ??= " \
    rsyslogd rsyslogrt inet regexp uuid \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
"

do_install_append(){
    install -m 0644 -D ${WORKDIR}/server.conf \
        ${D}${sysconfdir}/rsyslog.d/server.conf
}
