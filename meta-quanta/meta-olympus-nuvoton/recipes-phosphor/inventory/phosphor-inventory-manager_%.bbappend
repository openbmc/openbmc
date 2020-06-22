FILESEXTRAPATHS_prepend_olympus-nuvoton := "${THISDIR}/${PN}:"
PACKAGECONFIG_append_olympus-nuvoton = " associations"
SRC_URI_append_olympus-nuvoton = " file://associations.json"

DEPENDS_append_olympus-nuvoton = " olympus-nuvoton-inventory-cleanup"

do_install_append_olympus-nuvoton() {
    install -d ${D}${base_datadir}
    install -m 0755 ${WORKDIR}/associations.json ${D}${base_datadir}/associations.json
}
