FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " \
    file://rsyslog.conf \
    file://rsyslog.logrotate \
    file://rotate-event-logs.service \
    file://rotate-event-logs.sh \
    file://rsyslog-override.conf \
    file://server.conf \
"

FILES:${PN}:append:olympus-nuvoton = " ${systemd_system_unitdir}/rsyslog.service.d/rsyslog-override.conf"

PACKAGECONFIG:append:olympus-nuvoton = " imjournal"

do_install:append:olympus-nuvoton() {
        install -m 0644 ${WORKDIR}/rotate-event-logs.service ${D}${systemd_system_unitdir}
        install -d ${D}${systemd_system_unitdir}/rsyslog.service.d
        install -m 0644 ${WORKDIR}/rsyslog-override.conf \
                        ${D}${systemd_system_unitdir}/rsyslog.service.d/rsyslog-override.conf
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/rotate-event-logs.sh ${D}/${bindir}/rotate-event-logs.sh

        install -m 0644 -D ${WORKDIR}/server.conf \
        ${D}${sysconfdir}/rsyslog.d/server.conf 
}

SYSTEMD_SERVICE:${PN}:append:olympus-nuvoton = " rotate-event-logs.service"
