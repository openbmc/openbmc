FILESEXTRAPATHS:prepend := "${THISDIR}/linux-nuvoton:"

SRC_URI:append:evb-npcm845 = " file://evb-npcm845.cfg"
SRC_URI:append:evb-npcm845 = " file://enable-v4l2.cfg"
SRC_URI:append:evb-npcm845 = " file://luks.cfg"

SRC_URI:append:evb-npcm845 = " file://0001-dts-nuvoton-evb-npcm845-support-openbmc-partition.patch"
SRC_URI:append:evb-npcm845 = " file://0015-dts-add-reserved-memory.patch"

# SRC_URI:append:evb-npcm845 = " file://0002-dts-nuvoton-evb-npcm845-boot-from-fiu0-cs1.patch"
