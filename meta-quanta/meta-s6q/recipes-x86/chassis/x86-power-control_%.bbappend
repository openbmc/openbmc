FILESEXTRAPATHS:prepend:s6q := "${THISDIR}/${PN}:"
SRC_URI:append:s6q = " file://power-config-host0.json"

do_install:append:s6q() {
    install -m 0755 -d ${D}/${datadir}/${BPN}
    install -m 0644 ${UNPACKDIR}/power-config-host0.json ${D}${datadir}/${BPN}
}
