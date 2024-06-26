FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

require conf/recipes/fb-consoles.inc

SRC_URI += "file://rsyslog.conf \
           file://rsyslog.logrotate \
           file://rotate-event-logs.service \
           file://rotate-event-logs.timer \
           file://rsyslog-oob-console.conf.in \
"

PACKAGECONFIG:append = " imjournal klog imfile"

do_install:append() {
        install -m 0644 ${UNPACKDIR}/rotate-event-logs.service ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/rotate-event-logs.timer ${D}${systemd_system_unitdir}
        rm ${D}${sysconfdir}/rsyslog.d/imjournal.conf
        for host in ${OBMC_HOST_INSTANCES}; do
            host_name="host${host}"
            conf=${D}${sysconfdir}/rsyslog.d/${host_name}.conf
            install -m 644 ${UNPACKDIR}/rsyslog-oob-console.conf.in ${conf}
            sed -i "s/__OOB_CONSOLE_HOST__/${host_name}/g;" ${conf}
        done
}

SYSTEMD_SERVICE:${PN} += " rotate-event-logs.service rotate-event-logs.timer"
