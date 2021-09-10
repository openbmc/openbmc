DEPENDS:append:mtjade = " inventory-cleanup"

FILESEXTRAPATHS:prepend:mtjade := "${THISDIR}/${PN}:"
PACKAGECONFIG:append:mtjade = " associations"
SRC_URI:append:mtjade = " file://associations.json"

do_install:append:mtjade() {
    install -d ${D}${base_datadir}
    install -m 0755 ${WORKDIR}/associations.json ${D}${base_datadir}
}
