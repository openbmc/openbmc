FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"
SRC_URI_append_gbs = " file://config.json"
FILES_${PN}_append_gbs = " ${datadir}/binaryblob/config.json"

do_install_append_gbs() {
    install -d ${D}${datadir}/binaryblob/
    install ${WORKDIR}/config.json ${D}${datadir}/binaryblob/config.json
}
