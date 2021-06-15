FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"
SRC_URI_append_gbs = " file://config.txt"

FILES_${PN}_append_gbs = " ${datadir}/mac-address/config.txt"

do_install_append_gbs() {
    install -d ${D}${datadir}/mac-address
    install -m 0644 -D ${WORKDIR}/config.txt \
        ${D}${datadir}/mac-address/config.txt
}
