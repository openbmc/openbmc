FILESEXTRAPATHS:prepend:scm-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:scm-npcm845 = " file://chassis-nmisource.override.yml"
SRC_URI:append:scm-npcm845 = " file://sol-default.override.yml"
