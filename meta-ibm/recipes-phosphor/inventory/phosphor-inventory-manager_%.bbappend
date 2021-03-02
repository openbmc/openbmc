FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
PACKAGECONFIG_append_ibm-ac-server = " associations"
SRC_URI_append_ibm-ac-server = " file://associations.json"
DEPENDS_append_ibm-ac-server = " inventory-cleanup"

PACKAGECONFIG_append_rainier = " associations"
SRC_URI_append_rainier = " \
    file://ibm,rainier-2u_associations.json \
    file://ibm,rainier-4u_associations.json \
    file://ibm,everest_associations.json \
    "

do_install_append_ibm-ac-server() {
    install -d ${D}${base_datadir}
    install -m 0755 ${WORKDIR}/associations.json ${D}${base_datadir}/associations.json
}

do_install_append_rainier() {
    install -d ${D}${base_datadir}
    install -m 0755 ${WORKDIR}/ibm,rainier-2u_associations.json ${D}${base_datadir}/ibm,rainier-2u_associations.json
    install -m 0755 ${WORKDIR}/ibm,rainier-4u_associations.json ${D}${base_datadir}/ibm,rainier-4u_associations.json
    install -m 0755 ${WORKDIR}/ibm,everest_associations.json ${D}${base_datadir}/ibm,everest_associations.json
}
