FILESEXTRAPATHS_prepend_olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI_append_olympus-nuvoton = " file://ipmb-olympus-channels.json"
FILES_${PN}_append_olympus-nuvoton = " ${datadir}/ipmbbridge/ipmb-channels.json"

do_install_append_olympus-nuvoton() {
    install -d ${D}${datadir}/ipmbbridge
    install -m 0644 -D ${WORKDIR}/ipmb-olympus-channels.json \
        ${D}${datadir}/ipmbbridge/ipmb-channels.json
}

