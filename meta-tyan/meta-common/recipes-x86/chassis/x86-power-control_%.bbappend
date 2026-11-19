FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
SRC_URI:append = " \
    file://power-config-host0.json \
"

RDEPENDS:${PN}:append = " bash"

do_install:append() {
    install -d ${D}${datadir}/${BPN}
    install -m 0644 ${UNPACKDIR}/power-config-host0.json ${D}${datadir}/${BPN}
}
