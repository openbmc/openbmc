FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
PACKAGECONFIG ??= " \
    rsyslogd rsyslogrt inet regexp uuid \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
"

SRC_URI += " \
    file://rsyslog.conf \
    file://server.conf \
    file://imjournal.conf \
"

do_install:append(){
    install -m 0644 -D ${UNPACKDIR}/server.conf \
        ${D}${sysconfdir}/rsyslog.d/server.conf

    if ${@bb.utils.contains('PACKAGECONFIG', 'imjournal', 'true', 'false', d)}; then
        install -m 0644 -D ${UNPACKDIR}/imjournal.conf \
            ${D}${sysconfdir}/rsyslog.d/imjournal.conf
    fi
}
