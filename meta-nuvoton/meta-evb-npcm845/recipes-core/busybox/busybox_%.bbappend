FILESEXTRAPATHS:prepend:evb-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:evb-npcm845 = " file://lsusb.cfg"
SRC_URI:append:evb-npcm845 = " file://timeout.cfg"
SRC_URI:append:evb-npcm845 = " file://dd.cfg"
SRC_URI:append:evb-npcm845 = " file://head.cfg"
