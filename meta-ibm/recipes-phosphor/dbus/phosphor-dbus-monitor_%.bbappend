FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SYSTEMD_OVERRIDE:${PN}:append = " phosphor-dbus-monitor-snmp.conf:phosphor-dbus-monitor.service.d/phosphor-dbus-monitor-snmp.conf"
