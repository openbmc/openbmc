FILESEXTRAPATHS:prepend:evb-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:evb-npcm845 = " \
    file://0002-hwmon-temp-add-tmp100-support.patch \
    "
