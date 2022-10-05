FILESEXTRAPATHS:prepend:scm-npcm845 := "${THISDIR}/${PN}:"
PACKAGECONFIG:append:scm-npcm845 = " associations"
SRC_URI:append:scm-npcm845 = " file://associations.json"
DEPENDS:append:scm-npcm845 = " scm-npcm845-inventory-cleanup"

do_install:append:scm-npcm845() {
    install -d ${D}${base_datadir}
    install -m 0755 ${WORKDIR}/associations.json ${D}${base_datadir}/associations.json
}
