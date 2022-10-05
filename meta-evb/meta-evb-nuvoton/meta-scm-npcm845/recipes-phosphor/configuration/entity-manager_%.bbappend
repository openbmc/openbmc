FILESEXTRAPATHS:prepend:scm-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:scm-npcm845 = " file://aurea-scm.json"
SRC_URI:append:scm-npcm845 = " file://aurea-hpm.json"
SRC_URI:append:scm-npcm845 = " file://blacklist.json"

do_install:append:scm-npcm845() {
    rm -f ${D}/usr/share/entity-manager/configurations/*.json
    install -d ${D}${datadir}/entity-manager
    install -m 0644 -D ${WORKDIR}/blacklist.json\
        ${D}${datadir}/entity-manager/blacklist.json
    install -m 0644 -D ${WORKDIR}/aurea-scm.json \
        ${D}${datadir}/entity-manager/configurations/aurea-scm.json
    install -m 0644 -D ${WORKDIR}/aurea-hpm.json \
        ${D}${datadir}/entity-manager/configurations/aurea-hpm.json
}