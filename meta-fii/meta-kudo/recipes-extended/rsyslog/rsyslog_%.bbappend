FILESEXTRAPATHS_prepend_kudo := "${THISDIR}/${PN}:"

SRC_URI_append_kudo = " \
    file://rsyslog.conf \
    file://rsyslog.logrotate \
    file://rotate-event-logs.service \
    file://rotate-event-logs.sh \
    file://rsyslog-override.conf \
    "

FILES_${PN}_append_kudo = " ${systemd_system_unitdir}/rsyslog.service.d/rsyslog-override.conf"

PACKAGECONFIG_append_kudo = " imjournal"

do_install_append_kudo() {
    install -m 0644 ${WORKDIR}/rotate-event-logs.service ${D}${systemd_system_unitdir}
    install -d ${D}${systemd_system_unitdir}/rsyslog.service.d
    install -m 0644 ${WORKDIR}/rsyslog-override.conf \
    ${D}${systemd_system_unitdir}/rsyslog.service.d/rsyslog-override.conf
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/rotate-event-logs.sh ${D}/${bindir}/rotate-event-logs.sh
    rm ${D}${sysconfdir}/rsyslog.d/imjournal.conf
}

SYSTEMD_SERVICE_${PN}_append_kudo = " rotate-event-logs.service"
