FILESEXTRAPATHS:prepend:evb-npcm750 := "${THISDIR}/${PN}:"

inherit image_version

SRC_URI:append:evb-npcm750 = " file://channel_config.json"
SRC_URI:append:evb-npcm750 = " file://dev_id.json"

do_install:append:evb-npcm750() {
    install -m 0644 -D ${WORKDIR}/channel_config.json \
        ${D}${datadir}/ipmi-providers/channel_config.json
    install -m 0644 -D ${WORKDIR}/dev_id.json \
        ${D}${datadir}/ipmi-providers/dev_id.json
}
