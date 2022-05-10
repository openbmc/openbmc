FILESEXTRAPATHS:prepend:evb-npcm845 := "${THISDIR}/${PN}:"
SRC_URI:append:evb-npcm845 = " file://busybox.cfg"
