FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://rsyslog.conf \
           file://rsyslog.logrotate \
           file://rotate-event-logs.service \
           file://rotate-event-logs.timer \
"

PACKAGECONFIG:append = " imjournal"

do_install:append() {
        install -m 0644 ${WORKDIR}/rotate-event-logs.service ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/rotate-event-logs.timer ${D}${systemd_system_unitdir}
        rm ${D}${sysconfdir}/rsyslog.d/imjournal.conf
}

SYSTEMD_SERVICE:${PN} += " rotate-event-logs.service rotate-event-logs.timer"
