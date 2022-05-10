ATF_VERSION = "1.3"
SRCREV = "469101d48a1b0cb6c93ea5fe3bd3b32b46bd047e"
BRANCH = "nuvoton"
LIC_FILES_CHKSUM = "file://license.rst;md5=1dd070c98a281d18d9eefd938729b031"

include arm-trusted-firmware.inc

FILESEXTRAPATHS:prepend:npcm8xx = "${THISDIR}/${PN}:"
SRC_URI:append:npcm8xx= " file://0001-plat-nuvoton-npcm845x-fix-build-warning.patch \
                   file://0002-plat-nuvoton-npcm845x-fix-reboot-hang.patch \
                 "
