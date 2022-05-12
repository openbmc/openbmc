FILESEXTRAPATHS:prepend := "${THISDIR}/linux-nuvoton:"

SRC_URI:append:scm-npcm845 = " file://scm-npcm845.cfg"
SRC_URI:append:scm-npcm845 = " file://0001-kernel-scm-dts.patch"
SRC_URI:append:scm-npcm845 = " file://0002-dts-npcm8xx-add-psci-smp-method-tz.patch"
