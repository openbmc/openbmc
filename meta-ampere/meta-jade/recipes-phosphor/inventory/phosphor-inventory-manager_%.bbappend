DEPENDS_append_mtjade = " inventory-cleanup"

FILESEXTRAPATHS_prepend_mtjade := "${THISDIR}/${PN}:"
PACKAGECONFIG_append_mtjade = " associations"
SRC_URI_append_mtjade = " file://associations.json"

do_install_append_mtjade() {
    install -d ${D}${base_datadir}
    install -m 0755 ${WORKDIR}/associations.json ${D}${base_datadir}
}
