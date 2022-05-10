FILESEXTRAPATHS:prepend:evb-npcm845 := "${THISDIR}/${PN}:"
PACKAGECONFIG:append:evb-npcm845 = " associations"
SRC_URI:append:evb-npcm845 = " file://associations.json"
DEPENDS:append:evb-npcm845 = " evb-npcm845-inventory-cleanup"

do_install:append:evb-npcm845() {
    install -d ${D}${base_datadir}
    install -m 0755 ${WORKDIR}/associations.json ${D}${base_datadir}/associations.json
}
