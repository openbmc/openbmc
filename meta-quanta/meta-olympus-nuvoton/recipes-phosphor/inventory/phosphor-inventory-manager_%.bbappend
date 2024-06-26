FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"
PACKAGECONFIG:append:olympus-nuvoton = " associations"
SRC_URI:append:olympus-nuvoton = " file://associations.json"

DEPENDS:append:olympus-nuvoton = " olympus-nuvoton-inventory-cleanup"

do_install:append:olympus-nuvoton() {
    install -d ${D}${base_datadir}
    install -m 0755 ${UNPACKDIR}/associations.json ${D}${base_datadir}/associations.json
}
