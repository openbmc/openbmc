FILESEXTRAPATHS_prepend_kudo := "${THISDIR}/${PN}:"
SRC_URI_append_kudo = " file://config.json"
FILES_${PN}_append_kudo = " ${datadir}/binaryblob/config.json"

do_install_append_kudo() {
    install -d ${D}${datadir}/binaryblob/
    install ${WORKDIR}/config.json ${D}${datadir}/binaryblob/config.json
}
