SUMMARY = "Policy configuration for rsyslog"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

DEPENDS += "rsyslog"

FILES_${PN} += "${systemd_system_unitdir}/rsyslog.service.d/rsyslog-override.conf"

SRC_URI += "file://rsyslog-override.conf"

do_install() {
    install -d ${D}${systemd_system_unitdir}/rsyslog.service.d
    install -m 0644 ${WORKDIR}/rsyslog-override.conf \
        ${D}${systemd_system_unitdir}/rsyslog.service.d/rsyslog-override.conf
}
