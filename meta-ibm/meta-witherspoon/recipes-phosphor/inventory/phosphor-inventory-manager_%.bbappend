FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
PACKAGECONFIG_append_ibm-ac-server = " associations"
SRC_URI_append_ibm-ac-server = " file://associations.json"


DEPENDS_append_ibm-ac-server = " inventory-cleanup"

do_install_append_ibm-ac-server() {
    install -d ${D}${base_datadir}
    install -m 0755 ${WORKDIR}/associations.json ${D}${base_datadir}/associations.json
}
