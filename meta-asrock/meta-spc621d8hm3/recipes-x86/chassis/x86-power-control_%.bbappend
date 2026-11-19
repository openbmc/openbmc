FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://power-config-host0.json \
    "

do_install:append() {
    install -d  ${D}/${datadir}/${BPN}
    install -m 0644 ${UNPACKDIR}/power-config-host0.json ${D}/${datadir}/${BPN}
}
