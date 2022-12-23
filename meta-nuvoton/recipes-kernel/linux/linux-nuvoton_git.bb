
#KBRANCH ?= "dev-5.14"
#LINUX_VERSION ?= "5.14"
#SRCREV="e2413239f9a751a2a7491569d27dce76773f2777"

#KBRANCH ?= "NPCM-5.10-OpenBMC"
#LINUX_VERSION ?= "5.10.67"
#SRCREV = "d37182b55c65daa74fc42fc0efff77417ae07a62"

KBRANCH ?= "NPCM-5.15-OpenBMC"
LINUX_VERSION ?= "5.15.50"
SRCREV = "40bdbefc3e5833a9f537d8d43d9a83e72a2b3ac7"

require linux-nuvoton.inc

SRC_URI:append:nuvoton = " file://0003-i2c-nuvoton-npcm750-runbmc-integrate-the-slave-mqueu.patch"
SRC_URI:append:nuvoton = " file://0004-driver-ncsi-replace-del-timer-sync.patch"
#SRC_URI:append:nuvoton = " file://0015-driver-misc-nuvoton-vdm-support-openbmc-libmctp.patch"
SRC_URI:append:nuvoton = " file://0017-drivers-i2c-workaround-for-i2c-slave-behavior.patch"
SRC_URI:append:nuvoton = " file://0018-cerberus-mailbox-driver.patch"

# New Arch VDMX/VDMA driver
# SRC_URI:append:nuvoton = " file://2222-driver-misc-add-nuvoton-vdmx-vdma-driver.patch"
