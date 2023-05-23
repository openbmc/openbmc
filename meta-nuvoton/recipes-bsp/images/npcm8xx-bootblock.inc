SUMMARY = "Primary bootloader for NPCM8XX (Arbel) devices"
DESCRIPTION = "Primary bootloader for NPCM8XX (Arbel) devices"
HOMEPAGE = "https://github.com/Nuvoton-Israel/npcm8xx-bootblock"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

BB_TIP = "arbel_a35_bootblock.bin"
BB_NO_TIP = "arbel_a35_bootblock_no_tip.bin"

OUTPUT_BB_TIP_BIN    = "Images/tip"
OUTPUT_BB_NO_TIP_BIN = "Images/no_tip"

S = "${WORKDIR}/git"

SRC_URI = " \
    git://github.com/Nuvoton-Israel/npcm8xx-bootblock;branch=main;protocol=https"

inherit deploy

do_deploy () {
    if [ "${TIP_IMAGE}" = "True" ] ; then
        install -D -m 644 ${OUTPUT_BB_TIP_BIN}/${BB_TIP} ${DEPLOYDIR}/${BB_TIP}
    else
        install -D -m 644 ${OUTPUT_BB_NO_TIP_BIN}/${BB_NO_TIP} ${DEPLOYDIR}/${BB_NO_TIP}
    fi
}

addtask deploy before do_build after do_compile
