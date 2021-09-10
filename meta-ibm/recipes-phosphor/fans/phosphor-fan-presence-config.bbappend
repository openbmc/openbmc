FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:witherspoon = "file://config.json"
SRC_URI:p10bmc = "file://rainier-2u/config.json \
                   file://rainier-4u/config.json \
                   file://rainier-1s4u/config.json \
                   file://everest/config.json \
                   "

do_install:witherspoon() {
        install -d ${D}/${datadir}/phosphor-fan-presence/presence/
        install -m 0644 ${WORKDIR}/config.json ${D}/${datadir}/phosphor-fan-presence/presence/
}
do_install:p10bmc() {
        # Install Rainier-2U/4U fan presence config files
        install -d ${D}/${datadir}/phosphor-fan-presence/presence/ibm,rainier-2u/
        install -d ${D}/${datadir}/phosphor-fan-presence/presence/ibm,rainier-4u/
        install -d ${D}/${datadir}/phosphor-fan-presence/presence/ibm,rainier-1s4u/
        install -m 0644 ${WORKDIR}/rainier-2u/config.json ${D}/${datadir}/phosphor-fan-presence/presence/ibm,rainier-2u/
        install -m 0644 ${WORKDIR}/rainier-4u/config.json ${D}/${datadir}/phosphor-fan-presence/presence/ibm,rainier-4u/
        install -m 0644 ${WORKDIR}/rainier-1s4u/config.json ${D}/${datadir}/phosphor-fan-presence/presence/ibm,rainier-1s4u/

        # Install Everest fan presence config file
        install -d ${D}/${datadir}/phosphor-fan-presence/presence/ibm,everest/
        install -m 0644 ${WORKDIR}/everest/config.json ${D}/${datadir}/phosphor-fan-presence/presence/ibm,everest/
}

FILES:${PN}:append:witherspoon = " ${datadir}/phosphor-fan-presence/presence/config.json"
FILES:${PN}:remove:witherspoon = "${presence_datadir}/config.yaml"

FILES:${PN}:remove:p10bmc = "${presence_datadir}/config.yaml"
FILES:${PN}:append:p10bmc = " ${datadir}/phosphor-fan-presence/presence/ibm,rainier-2u/config.json"
FILES:${PN}:append:p10bmc = " ${datadir}/phosphor-fan-presence/presence/ibm,rainier-4u/config.json"
FILES:${PN}:append:p10bmc = " ${datadir}/phosphor-fan-presence/presence/ibm,rainier-1s4u/config.json"
FILES:${PN}:append:p10bmc = " ${datadir}/phosphor-fan-presence/presence/ibm,everest/config.json"
