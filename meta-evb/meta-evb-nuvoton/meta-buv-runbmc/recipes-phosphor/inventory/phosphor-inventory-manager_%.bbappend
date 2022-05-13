FILESEXTRAPATHS:prepend:buv-runbmc := "${THISDIR}/${PN}:"
PACKAGECONFIG:append:buv-runbmc = " associations"

SRC_URI:append:buv-runbmc = " file://associations.json"
DEPENDS:append:buv-runbmc = " buv-runbmc-inventory-cleanup"

do_install:append:buv-runbmc() {
    install -d ${D}${base_datadir}
    install -m 0755 ${WORKDIR}/associations.json ${D}${base_datadir}/associations.json
}
