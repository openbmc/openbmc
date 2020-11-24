FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI_witherspoon = "file://config.json"
SRC_URI_rainier = "file://rainier-2u/config.json \
                   file://rainier-4u/config.json \
                   file://everest/config.json \
                   "

do_install_witherspoon() {
        install -d ${D}/${datadir}/phosphor-fan-presence/presence/
        install -m 0644 ${WORKDIR}/config.json ${D}/${datadir}/phosphor-fan-presence/presence/
}
do_install_rainier() {
        # Install Rainier-2U/4U fan presence config files
        install -d ${D}/${datadir}/phosphor-fan-presence/presence/ibm,rainier-2u/
        install -d ${D}/${datadir}/phosphor-fan-presence/presence/ibm,rainier-4u/
        install -m 0644 ${WORKDIR}/rainier-2u/config.json ${D}/${datadir}/phosphor-fan-presence/presence/ibm,rainier-2u/
        install -m 0644 ${WORKDIR}/rainier-4u/config.json ${D}/${datadir}/phosphor-fan-presence/presence/ibm,rainier-4u/

        # Install Everest fan presence config file
        install -d ${D}/${datadir}/phosphor-fan-presence/presence/ibm,everest/
        install -m 0644 ${WORKDIR}/everest/config.json ${D}/${datadir}/phosphor-fan-presence/presence/ibm,everest/
}

FILES_${PN}_append_witherspoon = " ${datadir}/phosphor-fan-presence/presence/config.json"
FILES_${PN}_remove_witherspoon = "${presence_datadir}/config.yaml"

FILES_${PN}_remove_rainier = "${presence_datadir}/config.yaml"
FILES_${PN}_append_rainier = " ${datadir}/phosphor-fan-presence/presence/ibm,rainier-2u/config.json"
FILES_${PN}_append_rainier = " ${datadir}/phosphor-fan-presence/presence/ibm,rainier-4u/config.json"
FILES_${PN}_append_rainier = " ${datadir}/phosphor-fan-presence/presence/ibm,everest/config.json"
