SUMMARY = "Primary bootloader for NPCM8XX (Arbel) devices"
DESCRIPTION = "Primary bootloader for NPCM8XX (Arbel) devices"
HOMEPAGE = "https://github.com/Nuvoton-Israel/npcm8xx-bootblock"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

FILENAME = "arbel_a35_bootblock.${PV}.bin"

S = "${WORKDIR}/git"

SRC_URI = " \
    git://github.com/Nuvoton-Israel/npcm8xx-bootblock;branch=main;protocol=https \
    https://github.com/Nuvoton-Israel/npcm8xx-bootblock/releases/download/${RELEASE}/${FILENAME};downloadfilename=${FILENAME};name=bin \
"

inherit deploy

do_deploy () {
	install -D -m 644 ${WORKDIR}/${FILENAME} ${DEPLOYDIR}/arbel_a35_bootblock.bin
}

addtask deploy before do_build after do_compile
