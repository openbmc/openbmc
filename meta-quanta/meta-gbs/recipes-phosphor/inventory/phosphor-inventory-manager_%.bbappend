FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"
PACKAGECONFIG_append_gbs = " associations"
SRC_URI_append_gbs = " file://associations.json"
DEPENDS_append_gbs = " inventory-cleanup"

do_install_append_gbs() {
    install -d ${D}${base_datadir}
    install -m 0755 ${WORKDIR}/associations.json ${D}${base_datadir}/associations.json
}
