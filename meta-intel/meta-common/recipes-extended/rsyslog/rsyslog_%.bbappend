FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://rsyslog.conf \
           file://rsyslog.logrotate \
           file://rotate-event-logs.service \
           file://rotate-event-logs.timer \
           file://rsyslog-override.conf \
"

FILES_${PN} += "${systemd_system_unitdir}/rsyslog.service.d/rsyslog-override.conf"

PACKAGECONFIG_append = " imjournal"

do_install_append() {
        install -m 0644 ${WORKDIR}/rotate-event-logs.service ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/rotate-event-logs.timer ${D}${systemd_system_unitdir}
        install -d ${D}${systemd_system_unitdir}/rsyslog.service.d
        install -m 0644 ${WORKDIR}/rsyslog-override.conf \
                        ${D}${systemd_system_unitdir}/rsyslog.service.d/rsyslog-override.conf
        rm ${D}${sysconfdir}/rsyslog.d/imjournal.conf
}

SYSTEMD_SERVICE_${PN} += " rotate-event-logs.service rotate-event-logs.timer"
