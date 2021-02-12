SUMMARY = "Primary bootloader for NPCM7XX (Poleg) devices"
DESCRIPTION = "Primary bootloader for NPCM7XX (Poleg) devices"
HOMEPAGE = "https://github.com/Nuvoton-Israel/npcm7xx-bootblock"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

FILENAME = "Poleg_bootblock_${PV}.bin"

S = "${WORKDIR}"

SRCREV = "7bfef99f7a0354395519c4975f96d66cdda1fb67"
SRC_URI = " \
    https://raw.githubusercontent.com/Nuvoton-Israel/bootblock/${SRCREV}/LICENSE;name=lic \
    https://github.com/Nuvoton-Israel/bootblock/releases/download/BootBlock_${PV}/Poleg_bootblock_basic.bin;downloadfilename=${FILENAME};name=bin \
"

SRC_URI[lic.md5sum] = "b234ee4d69f5fce4486a80fdaf4a4263"
SRC_URI[bin.sha256sum] = "b2df56cc8667227fa625f11c628bfc10c831d36d076bf28ff376902d06b63a54"

inherit deploy

do_deploy () {
	install -D -m 644 ${WORKDIR}/${FILENAME} ${DEPLOYDIR}/Poleg_bootblock.bin
}

addtask deploy before do_build after do_compile
