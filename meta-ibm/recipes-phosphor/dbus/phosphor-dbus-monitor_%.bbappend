FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SYSTEMD_LINK:phosphor-msl-verify:append:ibm-ac-server = " ../phosphor-msl-verify.service:obmc-chassis-poweron@0.target.requires/phosphor-msl-verify.service"
SYSTEMD_OVERRIDE:${PN}:append = " phosphor-dbus-monitor-snmp.conf:phosphor-dbus-monitor.service.d/phosphor-dbus-monitor-snmp.conf"
