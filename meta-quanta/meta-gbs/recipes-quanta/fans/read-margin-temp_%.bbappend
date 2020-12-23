FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"
SRC_URI_append_gbs = " file://config-margin.json \
                       file://read-margin-temp-wait.sh \
                     "

FILES_${PN}_append_gbs = " ${bindir}/read-margin-temp-wait.sh"
FILES_${PN}_append_gbs = " ${datadir}/read-margin-temp/config-margin.json"

do_install_append_gbs() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/read-margin-temp-wait.sh ${D}${bindir}

    install -d ${D}${datadir}/read-margin-temp
    install -m 0644 -D ${WORKDIR}/config-margin.json \
        ${D}${datadir}/read-margin-temp/config-margin.json
}
