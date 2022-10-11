FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit image_version

SRC_URI:append:scm-npcm845 = " file://channel_config.json"
SRC_URI:append:scm-npcm845 = " file://dev_id.json"
SRC_URI:append:scm-npcm845 = " file://fw.json"
SRC_URI:append:scm-npcm845 = " file://system_guid.json"
SRC_URI:append:scm-npcm845 = " file://power_reading.json"

FILES:${PN}:append:scm-npcm845 = " ${datadir}/ipmi-providers/fw.json"
FILES:${PN}:append:scm-npcm845 = " ${datadir}/ipmi-providers/system_guid.json"

do_install:append:scm-npcm845() {
    install -m 0644 -D ${WORKDIR}/channel_config.json \
        ${D}${datadir}/ipmi-providers/channel_config.json
    install -m 0644 -D ${WORKDIR}/dev_id.json \
        ${D}${datadir}/ipmi-providers/dev_id.json
    install -m 0644 -D ${WORKDIR}/fw.json \
        ${D}${datadir}/ipmi-providers/fw.json
    install -m 0644 -D ${WORKDIR}/system_guid.json \
        ${D}${datadir}/ipmi-providers/system_guid.json
    install -m 0644 -D ${WORKDIR}/power_reading.json \
        ${D}${datadir}/ipmi-providers/power_reading.json
}
