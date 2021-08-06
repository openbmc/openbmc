FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:witherspoon = "file://config.json"
SRC_URI:p10bmc = "file://rainier-2u/config.json \
                   file://rainier-4u/config.json \
                   file://rainier-1s4u/config.json \
                   file://everest/config.json \
                   "

do_install:witherspoon() {
        install -d ${D}/${datadir}/phosphor-fan-presence/monitor/
        install -m 0644 ${WORKDIR}/config.json ${D}/${datadir}/phosphor-fan-presence/monitor/
}

do_install:p10bmc() {
        # Install Rainier-2U/4U fan monitor config files
        install -d ${D}/${datadir}/phosphor-fan-presence/monitor/ibm,rainier-2u/
        install -d ${D}/${datadir}/phosphor-fan-presence/monitor/ibm,rainier-4u/
        install -d ${D}/${datadir}/phosphor-fan-presence/monitor/ibm,rainier-1s4u/
        install -m 0644 ${WORKDIR}/rainier-2u/config.json ${D}/${datadir}/phosphor-fan-presence/monitor/ibm,rainier-2u/
        install -m 0644 ${WORKDIR}/rainier-4u/config.json ${D}/${datadir}/phosphor-fan-presence/monitor/ibm,rainier-4u/
        install -m 0644 ${WORKDIR}/rainier-1s4u/config.json ${D}/${datadir}/phosphor-fan-presence/monitor/ibm,rainier-1s4u/

        # Install Everest fan monitor config file
        install -d ${D}/${datadir}/phosphor-fan-presence/monitor/ibm,everest/
        install -m 0644 ${WORKDIR}/everest/config.json ${D}/${datadir}/phosphor-fan-presence/monitor/ibm,everest/
}

FILES:${PN}:append:witherspoon = " ${datadir}/phosphor-fan-presence/monitor/config.json"
FILES:${PN}:remove:witherspoon = "${monitor_datadir}/monitor.yaml"

FILES:${PN}:remove:p10bmc = "${monitor_datadir}/monitor.yaml"
FILES:${PN}:append:p10bmc = " ${datadir}/phosphor-fan-presence/monitor/ibm,rainier-2u/config.json"
FILES:${PN}:append:p10bmc = " ${datadir}/phosphor-fan-presence/monitor/ibm,rainier-4u/config.json"
FILES:${PN}:append:p10bmc = " ${datadir}/phosphor-fan-presence/monitor/ibm,rainier-1s4u/config.json"
FILES:${PN}:append:p10bmc = " ${datadir}/phosphor-fan-presence/monitor/ibm,everest/config.json"
