FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit image_version

SRC_URI:append:evb-npcm845 = " file://channel_config.json"
SRC_URI:append:evb-npcm845 = " file://dev_id.json"

do_install:append:evb-npcm845() {
    install -m 0644 -D ${WORKDIR}/channel_config.json \
        ${D}${datadir}/ipmi-providers/channel_config.json
    install -m 0644 -D ${WORKDIR}/dev_id.json \
        ${D}${datadir}/ipmi-providers/dev_id.json
}
