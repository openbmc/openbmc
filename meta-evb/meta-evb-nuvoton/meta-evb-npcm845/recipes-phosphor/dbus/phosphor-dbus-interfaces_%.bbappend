FILESEXTRAPATHS:prepend:evb-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:evb-npcm845 = " file://0001-add-Enabled-property-and-remove-nmiEnable-method.patch"
