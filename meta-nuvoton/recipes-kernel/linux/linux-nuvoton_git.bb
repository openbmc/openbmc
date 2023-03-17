KBRANCH:npcm7xx = "NPCM-5.15-OpenBMC"
LINUX_VERSION:npcm7xx = "5.15.85"
SRCREV:npcm7xx = "9701d3eef021f3c49ac710f4b82d78d584dd1952"

KBRANCH:npcm8xx = "NPCM-6.1-OpenBMC"
LINUX_VERSION:npcm8xx = "6.1.12"
SRCREV:npcm8xx = "c84e3090cebe5c889d61859b24a357928bfbae00"

require linux-nuvoton.inc

SRC_URI:append:nuvoton = " file://0003-i2c-nuvoton-npcm750-runbmc-integrate-the-slave-mqueu.patch"
SRC_URI:append:nuvoton = " file://0017-drivers-i2c-workaround-for-i2c-slave-behavior.patch"

# New Arch VDMX/VDMA driver
# SRC_URI:append:nuvoton = " file://2222-driver-misc-add-nuvoton-vdmx-vdma-driver.patch"

# SRC_URI:append:nuvoton = " file://0004-driver-ncsi-replace-del-timer-sync.patch"
# SRC_URI:append:nuvoton = " file://0015-driver-misc-nuvoton-vdm-support-openbmc-libmctp.patch"
