FILESEXTRAPATHS:prepend:buv-runbmc := "${THISDIR}/${PN}:"

SRC_URI:append:buv-runbmc = " \
        file://rsyslog.conf \
        file://rsyslog.logrotate \
        file://rotate-event-logs.service \
        file://rotate-event-logs.sh \
        file://rsyslog-override.conf \
"

FILES:${PN}:append:buv-runbmc = " ${systemd_system_unitdir}/rsyslog.service.d/rsyslog-override.conf"

PACKAGECONFIG:append:buv-runbmc = " imjournal"

do_install:append:buv-runbmc() {
        install -m 0644 ${WORKDIR}/rotate-event-logs.service ${D}${systemd_system_unitdir}
        install -d ${D}${systemd_system_unitdir}/rsyslog.service.d
        install -m 0644 ${WORKDIR}/rsyslog-override.conf \
                        ${D}${systemd_system_unitdir}/rsyslog.service.d/rsyslog-override.conf
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/rotate-event-logs.sh ${D}/${bindir}/rotate-event-logs.sh
}

SYSTEMD_SERVICE:${PN}:append:buv-runbmc = " rotate-event-logs.service"
