SUMMARY = "TIP FW for NPCM8XX (Arbel) devices"
DESCRIPTION = "TIP FW for NPCM8XX (Arbel) devices"
HOMEPAGE = "https://github.com/Nuvoton-Israel/npcm8xx-tip-fw"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f789971f29c65e31dbb33ed209b4dc91"

FILENAME = "Kmt_TipFwL0_Skmt_TipFwL1_${OUTPUT_BIN}.bin"

S = "${WORKDIR}"

SRC_URI = " \
    https://github.com/Nuvoton-Israel/npcm8xx-tip-fw/raw/${SRCREV}/LICENSE;name=lic \
    https://github.com/Nuvoton-Israel/npcm8xx-tip-fw/raw/${SRCREV}/${OUTPUT_BIN}/Kmt_TipFwL0_Skmt_TipFwL1.bin;downloadfilename=${FILENAME};name=${OUTPUT_BIN} \
"

SRC_URI[lic.sha256sum] = "84f16976acf8bdb73798989594d8547419096fb9be29a0fd84d1eba0fd550049"

inherit deploy

do_deploy () {
    install -D -m 644 ${WORKDIR}/${FILENAME} ${DEPLOYDIR}/Kmt_TipFwL0_Skmt_TipFwL1.bin
}

addtask deploy before do_build after do_compile
