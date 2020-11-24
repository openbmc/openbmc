FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI_witherspoon = "file://config.json"
SRC_URI_rainier = "file://rainier-2u/config.json \
                   file://rainier-4u/config.json \
                   file://everest/config.json \
                   "

do_install_witherspoon() {
        install -d ${D}/${datadir}/phosphor-fan-presence/monitor/
        install -m 0644 ${WORKDIR}/config.json ${D}/${datadir}/phosphor-fan-presence/monitor/
}

do_install_rainier() {
        # Install Rainier-2U/4U fan monitor config files
        install -d ${D}/${datadir}/phosphor-fan-presence/monitor/ibm,rainier-2u/
        install -d ${D}/${datadir}/phosphor-fan-presence/monitor/ibm,rainier-4u/
        install -m 0644 ${WORKDIR}/rainier-2u/config.json ${D}/${datadir}/phosphor-fan-presence/monitor/ibm,rainier-2u/
        install -m 0644 ${WORKDIR}/rainier-4u/config.json ${D}/${datadir}/phosphor-fan-presence/monitor/ibm,rainier-4u/

        # Install Everest fan monitor config file
        install -d ${D}/${datadir}/phosphor-fan-presence/monitor/ibm,everest/
        install -m 0644 ${WORKDIR}/everest/config.json ${D}/${datadir}/phosphor-fan-presence/monitor/ibm,everest/
}

FILES_${PN}_append_witherspoon = " ${datadir}/phosphor-fan-presence/monitor/config.json"
FILES_${PN}_remove_witherspoon = "${monitor_datadir}/monitor.yaml"

FILES_${PN}_remove_rainier = "${monitor_datadir}/monitor.yaml"
FILES_${PN}_append_rainier = " ${datadir}/phosphor-fan-presence/monitor/ibm,rainier-2u/config.json"
FILES_${PN}_append_rainier = " ${datadir}/phosphor-fan-presence/monitor/ibm,rainier-4u/config.json"
FILES_${PN}_append_rainier = " ${datadir}/phosphor-fan-presence/monitor/ibm,everest/config.json"
