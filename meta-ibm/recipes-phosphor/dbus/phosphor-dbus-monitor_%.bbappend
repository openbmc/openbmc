FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SYSTEMD_OVERRIDE:${PN}:append = "${@bb.utils.contains('DISTRO_FEATURES', 'phosphor-no-snmp', '', ' phosphor-dbus-monitor-snmp.conf:phosphor-dbus-monitor.service.d/phosphor-dbus-monitor-snmp.conf', d)}"
