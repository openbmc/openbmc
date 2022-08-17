SUMMARY = "Image Generation and Programming Scripts for NPCM8XX (Arbel) devices"
DESCRIPTION = "Image Generation and Programming Scripts for NPCM8XX (Arbel) devices"
HOMEPAGE = "https://github.com/Nuvoton-Israel/igps-npcm8xx"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = " \
    git://github.com/Nuvoton-Israel/igps-npcm8xx;branch=main;protocol=https \
    file://0001-Adjust-paths-for-use-with-Bitbake.patch \
"

# tag IGPS_03.06.04
SRCREV = "e4bf449d68b316323a4ea8d9f3b81aac5ec235b0"

S = "${WORKDIR}/git"

DEST = "${D}${datadir}/${BPN}"

inherit deploy

do_deploy () {
	install -D -m 644 ${S}/py_scripts/ImageGeneration/output_binaries/Secure/Kmt_TipFwL0_Skmt_TipFwL1.bin ${DEPLOYDIR}/Kmt_TipFwL0_Skmt_TipFwL1.bin
}

addtask deploy before do_build after do_compile

do_install() {
	install -d ${DEST}
	install py_scripts/ImageGeneration/references/BootBlockAndHeader_${DEVICE_GEN}_${IGPS_MACHINE}.xml ${DEST}
	install py_scripts/ImageGeneration/references/UbootHeader_${DEVICE_GEN}.xml ${DEST}
	install py_scripts/ImageGeneration/inputs/BL31_AndHeader.xml ${DEST}
	install py_scripts/ImageGeneration/inputs/OpTeeAndHeader.xml ${DEST}
}

inherit native
