FILESEXTRAPATHS:prepend := "${THISDIR}/linux-nuvoton:"

SRC_URI:append:scm-npcm845 = " file://nuvoton-npcm845-scm.dts;subdir=git/arch/${ARCH}/boot/dts/nuvoton "
SRC_URI:append:scm-npcm845 = " file://0001-kernel-scm-dts.patch"
SRC_URI:append:scm-npcm845 = " file://0002-rtl8211f-customized-led.patch"
SRC_URI:append:scm-npcm845 = " file://scm-npcm845.cfg"
SRC_URI:append:scm-npcm845 = " file://enable-v4l2-kvm.cfg"
