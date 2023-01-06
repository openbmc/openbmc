FILESEXTRAPATHS:prepend := "${THISDIR}/linux-nuvoton:"

SRC_URI:append:scm-npcm845 = " file://0001-net-phy-realtek-add-soft_reset.patch"
SRC_URI:append:scm-npcm845 = " file://0002-hwmon-pmbus-support-p2011.patch"
SRC_URI:append:scm-npcm845 = " file://0004-rtl8211f-customized-led.patch"
SRC_URI:append:scm-npcm845 = " file://0005-Add-pmbus-driver-for-MAX16550.patch"

SRC_URI:append:scm-npcm845 = " file://0099-kernel-scm-dts.patch"
SRC_URI:append:scm-npcm845 = " file://0100-dts-scm-npcm845-add-tip-mailbox-support.patch"
#SRC_URI:append:scm-npcm845 = " file://0101-dts-npcm845-scm-add-ftpm-tee.patch"
SRC_URI:append:scm-npcm845 = " file://0102-add-reserved-memory-for-gfx-optee-tip-cp-tcg-log.patch"

SRC_URI:append:scm-npcm845 = " file://scm-npcm845.cfg"
SRC_URI:append:scm-npcm845 = " file://enable-v4l2-kvm.cfg"

