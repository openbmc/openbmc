SUMMARY = "Primary bootloader for NPCM7XX (Poleg) devices"
DESCRIPTION = "Primary bootloader for NPCM7XX (Poleg) devices"
HOMEPAGE = "https://github.com/Nuvoton-Israel/npcm7xx-bootblock"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

FILENAME = "Poleg_bootblock_${PV}.bin"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

SRCREV = "cac8f0ed8c6e875148ec37b657fcbdd1058c2c69"
SRC_URI = " \
    https://raw.githubusercontent.com/Nuvoton-Israel/bootblock/${SRCREV}/LICENSE;name=lic \
    https://github.com/Nuvoton-Israel/bootblock/releases/download/BootBlock_${PV}/Poleg_bootblock_basic.bin;downloadfilename=${FILENAME};name=bin \
"

SRC_URI[lic.md5sum] = "b234ee4d69f5fce4486a80fdaf4a4263"
SRC_URI[bin.sha256sum] = "1bc367032b2ac76190064256a306993a0405de2a2af5b4fad2c549ccd6a0ba0f"

inherit deploy

do_deploy () {
	install -D -m 644 ${UNPACKDIR}/${FILENAME} ${DEPLOYDIR}/Poleg_bootblock.bin
}

addtask deploy before do_build after do_compile
