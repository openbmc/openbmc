FILESEXTRAPATHS_prepend_runbmc-nuvoton := "${THISDIR}/${PN}:"

SRC_URI_append_runbmc-nuvoton = " file://ipmb-runbmc-channels.json"
FILES_${PN}_append_runbmc-nuvoton = " ${datadir}/ipmbbridge/ipmb-channels.json"

do_install_append_runbmc-nuvoton() {
    install -d ${D}${datadir}/ipmbbridge
    install -m 0644 -D ${WORKDIR}/ipmb-runbmc-channels.json \
        ${D}${datadir}/ipmbbridge/ipmb-channels.json
}

