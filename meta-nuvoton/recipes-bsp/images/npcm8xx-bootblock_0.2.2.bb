SUMMARY = "Primary bootloader for NPCM8XX (Arbel) devices"
DESCRIPTION = "Primary bootloader for NPCM8XX (Arbel) devices"
HOMEPAGE = "https://github.com/Nuvoton-Israel/npcm8xx-bootblock"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=650b869bd8ff2aed59c62bad2a22a821"

FILENAME = "arbel_a35_bootblock_${PV}.bin"

S = "${WORKDIR}"

SRCREV = "4002b2f086f6d2177ec36bed507241386d604f6b"
SRC_URI = " \
    https://github.com/Nuvoton-Israel/npcm8xx-bootblock/${SRCREV}/LICENSE;name=lic \
    https://github.com/Nuvoton-Israel/npcm8xx-bootblock/releases/download/A35_BootBlock_${PV}/arbel_a35_bootblock.bin;downloadfilename=${FILENAME};name=bin \
"

SRC_URI[lic.sha256sum] = "7c34d28e784b202aa4998f477fd0aa9773146952d7f6fa5971369fcdda59cf48"
SRC_URI[bin.sha256sum] = "f5962cad0c18a2ec8956adfa79298802bc641293f3eb96f97644485f6a46b3b1"

inherit deploy

do_deploy () {
	install -D -m 644 ${WORKDIR}/${FILENAME} ${DEPLOYDIR}/arbel_a35_bootblock.bin
}

addtask deploy before do_build after do_compile
