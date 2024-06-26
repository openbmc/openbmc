FILESEXTRAPATHS:prepend:gbs := "${THISDIR}/${PN}:"
SRC_URI:append:gbs = " file://power-config-host0.json \
                       file://chassis-system-reset.service \
                     "

EXTRA_OECMAKE:append:gbs = " -DCHASSIS_SYSTEM_RESET=ON"

RDEPENDS:${PN}:append:gbs = " bash"
RDEPENDS:${PN}:append:gbs = " gbs-hotswap-power-cycle"

do_install:append:gbs() {
    install -d ${D}${datadir}/${PN}
    install -m 0644 ${UNPACKDIR}/power-config-host0.json ${D}${datadir}/${PN}
}
