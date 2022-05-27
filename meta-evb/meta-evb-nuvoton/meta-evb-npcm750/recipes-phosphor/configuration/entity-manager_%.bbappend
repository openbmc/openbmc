FILESEXTRAPATHS:prepend:evb-npcm750 := "${THISDIR}/${PN}:"

SRC_URI:append:evb-npcm750 = " file://F0B_BMC_BMC.json"

FILES:${PN}:append:evb-npcm750 = " \
    ${datadir}/entity-manager/F0B_BMC_BMC.json"

do_install:append:evb-npcm750() {
    install -d ${D}${datadir}/entity-manager
    install -m 0644 -D ${WORKDIR}/F0B_BMC_BMC.json \
        ${D}${datadir}/entity-manager/configurations/F0B_BMC_BMC.json
}
