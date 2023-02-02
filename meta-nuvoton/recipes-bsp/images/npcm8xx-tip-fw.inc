SUMMARY = "TIP FW for NPCM8XX (Arbel) devices"
DESCRIPTION = "TIP FW for NPCM8XX (Arbel) devices"
HOMEPAGE = "https://github.com/Nuvoton-Israel/npcm8xx-tip-fw"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f789971f29c65e31dbb33ed209b4dc91"

TIP_FW = "Kmt_TipFwL0_Skmt_TipFwL1.bin"
S = "${WORKDIR}/git"

SRC_URI = "git://github.com/Nuvoton-Israel/npcm8xx-tip-fw;branch=main;protocol=https"

inherit deploy
do_deploy () {
    install -D -m 644 ${OUTPUT_DEV}/${TIP_FW} ${DEPLOYDIR}/${TIP_FW}
}

addtask deploy before do_build after do_compile
