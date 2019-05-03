FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
PACKAGECONFIG_append = " associations"
SRC_URI += "file://associations.json"


DEPENDS_append = " inventory-cleanup"

do_install_append() {
    install -d ${D}${base_datadir}
    install -m 0755 ${WORKDIR}/associations.json ${D}${base_datadir}/associations.json
}
