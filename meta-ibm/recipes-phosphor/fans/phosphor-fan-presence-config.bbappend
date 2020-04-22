FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI_witherspoon = "file://config.json"

do_install_witherspoon() {
        install -d ${D}/${datadir}/phosphor-fan-presence/presence/
        install -m 0644 ${WORKDIR}/config.json ${D}/${datadir}/phosphor-fan-presence/presence/
}

FILES_${PN}_append_witherspoon = " ${datadir}/phosphor-fan-presence/presence/config.json"
FILES_${PN}_remove_witherspoon = "${presence_datadir}/config.yaml"
