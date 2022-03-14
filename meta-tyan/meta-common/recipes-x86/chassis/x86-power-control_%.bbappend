FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append = " \
    file://power-config-host0.json \
"

RDEPENDS:${PN}:append = " bash"

do_install:append() {
    install -d ${D}${datadir}/${PN}
    install -m 0644 ${WORKDIR}/power-config-host0.json ${D}${datadir}/${PN}
}
