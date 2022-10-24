SUMMARY = "TIP FW for NPCM8XX (Arbel) devices"
DESCRIPTION = "TIP FW for NPCM8XX (Arbel) devices"
HOMEPAGE = "https://github.com/Nuvoton-Israel/npcm8xx-tip-fw"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=650b869bd8ff2aed59c62bad2a22a821"

FILENAME = "Kmt_TipFwL0_Skmt_TipFwL1_${PV}.bin"

S = "${WORKDIR}"

SRCREV = "04e43fbacdc55860153444e04672097e3f2b29fa"
SRC_URI = " \
    https://github.com/Nuvoton-Israel/npcm8xx-tip-fw/${SRCREV}/LICENSE;name=lic \
    https://github.com/Nuvoton-Israel/npcm8xx-tip-fw/releases/download/TIP_FW_L0_0.4.6_L1_0.3.5/Kmt_TipFwL0_Skmt_TipFwL1.bin;downloadfilename=${FILENAME};name=bin \
"

SRC_URI[lic.sha256sum] = "7c34d28e784b202aa4998f477fd0aa9773146952d7f6fa5971369fcdda59cf48"
SRC_URI[bin.sha256sum] = "f7229664b05d506df90a110dd64bcfa7fa409da254ffc9f6d7367a845a75d8aa"

inherit deploy

do_deploy () {
	install -D -m 644 ${WORKDIR}/${FILENAME} ${DEPLOYDIR}/Kmt_TipFwL0_Skmt_TipFwL1.bin
}

addtask deploy before do_build after do_compile
