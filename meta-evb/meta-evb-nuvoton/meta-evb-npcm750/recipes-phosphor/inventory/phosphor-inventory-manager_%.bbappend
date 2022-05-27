FILESEXTRAPATHS:prepend:evb-npcm750 := "${THISDIR}/${PN}:"
PACKAGECONFIG:append:evb-npcm750 = " associations"

SRC_URI:append:evb-npcm750 = " file://associations.json"

do_install:append:evb-npcm750() {
    install -d ${D}${base_datadir}
    install -m 0755 ${WORKDIR}/associations.json ${D}${base_datadir}/associations.json
}
