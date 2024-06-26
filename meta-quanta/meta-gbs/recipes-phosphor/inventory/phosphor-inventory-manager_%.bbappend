FILESEXTRAPATHS:prepend:gbs := "${THISDIR}/${PN}:"
PACKAGECONFIG:append:gbs = " associations"
SRC_URI:append:gbs = " file://associations.json"
DEPENDS:append:gbs = " gbs-inventory-cleanup"

do_install:append:gbs() {
    install -d ${D}${base_datadir}
    install -m 0755 ${UNPACKDIR}/associations.json ${D}${base_datadir}/associations.json
}
