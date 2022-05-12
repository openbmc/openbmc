FILESEXTRAPATHS:prepend:scm-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:scm-npcm845 = " file://ipmb-channels.json"
FILES:${PN}:append:scm-npcm845 = " ${datadir}/ipmbbridge/ipmb-channels.json"

do_install:append:scm-npcm845() {
    install -d ${D}${datadir}/ipmbbridge
    install -m 0644 -D ${WORKDIR}/ipmb-channels.json \
        ${D}${datadir}/ipmbbridge/ipmb-channels.json
}

