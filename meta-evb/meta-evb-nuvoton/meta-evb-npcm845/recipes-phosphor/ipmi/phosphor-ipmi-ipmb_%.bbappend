FILESEXTRAPATHS:prepend:evb-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:evb-npcm845 = " file://ipmb-channels.json"
FILES:${PN}:append:evb-npcm845 = " ${datadir}/ipmbbridge/ipmb-channels.json"

do_install:append:evb-npcm845() {
    install -d ${D}${datadir}/ipmbbridge
    install -m 0644 -D ${WORKDIR}/ipmb-channels.json \
        ${D}${datadir}/ipmbbridge/ipmb-channels.json
}

