FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"
SRC_URI_append_gbs = " file://power-config-host0.json \
                       file://chassis-system-reset.service \
                     "

EXTRA_OECMAKE_append_gbs = " -DCHASSIS_SYSTEM_RESET=ON"

RDEPENDS_${PN}_append_gbs = " bash"
RDEPENDS_${PN}_append_gbs = " gbs-hotswap-power-cycle"

do_install_append_gbs() {
    install -d ${D}${datadir}/${PN}
    install -m 0644 ${WORKDIR}/power-config-host0.json ${D}${datadir}/${PN}
}
