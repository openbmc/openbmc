SUMMARY = "Phosphor systemd configuration overrides"
DESCRIPTION = "Overrides for systemd and its applications"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

DEPENDS += "systemd"

SRC_URI = "file://service-restart-policy.conf"
SRC_URI += "file://disable-duplicate-kernel-msg.conf"
SRC_URI += "file://disable-forward-to-syslog.conf"

FILES_${PN} += "${systemd_unitdir}/system.conf.d/service-restart-policy.conf"
FILES_${PN} += "${systemd_unitdir}/journald.conf.d/disable-duplicate-kernel-msg.conf"
FILES_${PN} += "${systemd_unitdir}/journald.conf.d/disable-forward-to-syslog.conf"


do_install() {
        install -m 644 -D ${WORKDIR}/service-restart-policy.conf ${D}${systemd_unitdir}/system.conf.d/service-restart-policy.conf
        install -m 644 -D ${WORKDIR}/disable-duplicate-kernel-msg.conf ${D}${systemd_unitdir}/journald.conf.d/disable-duplicate-kernel-msg.conf
        install -m 644 -D ${WORKDIR}/disable-forward-to-syslog.conf ${D}${systemd_unitdir}/journald.conf.d/disable-forward-to-syslog.conf
}
