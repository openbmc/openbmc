FILESEXTRAPATHS_prepend_olympus-nuvoton := "${THISDIR}/files:"

SRC_URI_append_olympus-nuvoton = " file://config.txt"

FILES_${PN}_append_olympus-nuvoton = " ${datadir}/mac-address/config.txt"

do_install_append_olympus-nuvoton() {
    install -d ${D}${datadir}/mac-address
    install -m 0644 -D ${WORKDIR}/config.txt \
        ${D}${datadir}/mac-address/config.txt
}
