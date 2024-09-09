HOMEPAGE = "https://github.com/Nuvoton-Israel/u-boot"
SECTION = "bootloaders"
DEPENDS += "flex-native bison-native"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

UBRANCH = "npcm-v2023.10"
SRC_URI = "git://github.com/Nuvoton-Israel/u-boot.git;branch=${UBRANCH};protocol=https"
SRCREV = "1f004b08f0a2cea41f141e8fe1a6d20923094d1b"

S = "${WORKDIR}/git"

PV = "v2023.10+git${SRCPV}"
