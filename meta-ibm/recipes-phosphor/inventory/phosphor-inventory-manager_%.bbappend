FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
PACKAGECONFIG:append:ibm-ac-server = " associations"
SRC_URI:append:ibm-ac-server = " file://associations.json"
DEPENDS:append:ibm-ac-server = " inventory-cleanup"

PACKAGECONFIG:append:p10bmc = " associations"
SRC_URI:append:p10bmc = " \
    file://ibm,rainier-2u_associations.json \
    file://ibm,rainier-4u_associations.json \
    file://ibm,everest_associations.json \
    "

do_install:append:ibm-ac-server() {
    install -d ${D}${base_datadir}
    install -m 0755 ${WORKDIR}/associations.json ${D}${base_datadir}/associations.json
}

do_install:append:p10bmc() {
    install -d ${D}${base_datadir}
    install -m 0755 ${WORKDIR}/ibm,rainier-2u_associations.json ${D}${base_datadir}/ibm,rainier-2u_associations.json
    install -m 0755 ${WORKDIR}/ibm,rainier-4u_associations.json ${D}${base_datadir}/ibm,rainier-4u_associations.json
    install -m 0755 ${WORKDIR}/ibm,everest_associations.json ${D}${base_datadir}/ibm,everest_associations.json
}
