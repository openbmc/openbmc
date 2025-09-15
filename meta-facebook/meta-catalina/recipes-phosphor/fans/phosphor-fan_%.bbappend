FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:remove = "sensor-monitor"
SYSTEMD_SERVICE:${PN}-sensor-monitor:remove = "sensor-monitor.service"
SYSTEMD_LINK:${PN}-sensor-monitor = ""

EXTRA_OEMESON += " -Dmonitor-use-host-state=enabled"

SRC_URI:append = " \
    file://config.json \
    "

do_install:append() {
    install -d ${D}${datadir}/phosphor-fan-presence/monitor
    install -m 0644 ${UNPACKDIR}/config.json ${D}${datadir}/phosphor-fan-presence/monitor/config.json
}
