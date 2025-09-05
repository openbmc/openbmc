FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = "
    file://power-config-host0.json \
"
POWER_CONFIG = "power-config-host0.json"

do_install:append() {
    install -d  ${D}/${datadir}/${PN}
    install -m 0644 ${UNPACKDIR}/power-config-host0.json ${D}/${datadir}/${PN}
}
