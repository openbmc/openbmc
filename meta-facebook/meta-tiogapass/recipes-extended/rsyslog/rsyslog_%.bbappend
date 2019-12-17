FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# Pin to rsyslog v8.1904.0 for now
# v8.1903.0 has a couple issues with the imjournal module:
#   1. Enabling the "WorkAroundJournalBug" option causes rsyslogd to fail to start
#   2. Logging data especially while rotating the journal causes a double free error
PV = "8.1904.0"
SRC_URI[md5sum] = "b9398b5aa68a829bf2c18a87490d30c0"
SRC_URI[sha256sum] = "7098b459dfc3f8bfc35d5b114c56e7945614ba76efa4e513b1db9c38b0ff9c3d"

SRC_URI += "file://rsyslog.conf \
           file://rsyslog.logrotate \
           file://rotate-event-logs.service \
           file://rotate-event-logs.timer \
"

PACKAGECONFIG_append = " imjournal"

do_install_append() {
        install -m 0644 ${WORKDIR}/rotate-event-logs.service ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/rotate-event-logs.timer ${D}${systemd_system_unitdir}
        rm ${D}${sysconfdir}/rsyslog.d/imjournal.conf
}

SYSTEMD_SERVICE_${PN} += " rotate-event-logs.service rotate-event-logs.timer"
