FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI_witherspoon += "file://monitor.yaml"
SRC_URI_witherspoon += "file://config.json"

do_install_append_witherspoon() {
        DEST=${D}${monitor_datadir}
        install -D monitor.yaml ${D}${monitor_datadir}/monitor.yaml
        install -d ${D}/${datadir}/phosphor-fan-presence/monitor/
        install -m 0644 ${WORKDIR}/config.json ${D}/${datadir}/phosphor-fan-presence/monitor/
}

FILES_${PN}_witherspoon += "${datadir}/phosphor-fan-presence/monitor/config.json"
FILES_${PN}_witherspoon += "${monitor_datadir}/monitor.yaml"
