SUMMARY = "Nuvoton NPCM8XX bootloader"
DESCRIPTION = "This is front end recipe for NPCM8XX IGPS. It replace \
original IGPS recipe which need implement many redundant function in \
recipe or class. After we add some hook in IGPS, now we can generate \
full bootbloader by IGPS script. We only need collect all built images \
from deploy folder, put them to IGPS input folder, and run script."
HOMEPAGE = "https://github.com/Nuvoton-Israel/igps-npcm8xx"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

IGPS_BRANCH ?= "main"
SRC_URI = " \
    git://github.com/Nuvoton-Israel/igps-npcm8xx;branch=${IGPS_BRANCH};protocol=https \
"
SRCREV = "f6ecbafba1008a88a27cffec53dea80e357565f0"

S = "${WORKDIR}/git"

DEPENDS = "npcm7xx-bingo-native openssl-native"
inherit obmc-phosphor-utils
inherit python3native deploy
FILE_FMT = "file://{}"

# Sign keys, replace them for production
IGPS_KEYS = ""
# Configuration files, clean them if no need
IGPS_CSVS = ""
IGPS_SETTINGS = "settings.json"
IGPS_CONFS = "${IGPS_KEYS} ${IGPS_CSVS} ${IGPS_SETTINGS}"
SRC_URI += "${@compose_list(d, 'FILE_FMT', 'IGPS_CONFS')}"

IGPS_SCRIPT_BASE = "${S}/py_scripts/ImageGeneration"
BB_BIN = "arbel_a35_bootblock"
BB_BIN .= "${@'_no_tip.bin' if d.getVar("TIP_IMAGE") != 'True' else '.bin'}"

do_configure[dirs] = "${WORKDIR}"
do_configure() {
    KEY_FOLDER=${IGPS_SCRIPT_BASE}/keys/openssl
    CSV_FOLDER=${IGPS_SCRIPT_BASE}/inputs/registers
    # keys
    install -d ${KEY_FOLDER}
    if [ -n "${IGPS_KEYS}" ];then
        cp -v ${IGPS_KEYS} ${KEY_FOLDER}
    fi

    # csv files
    install -d ${CSV_FOLDER}
    if [ -n "${IGPS_CSVS}" ];then
        cp -v ${IGPS_CSVS} ${CSV_FOLDER}
    fi

    # change customized settings for XML and key setting
    if [ -n "${IGPS_SETTINGS}" ];then
        cd ${S}
        python3 ${IGPS_SCRIPT_BASE}/config_replacer.py ${WORKDIR}/${IGPS_SETTINGS}
    fi
}

do_compile[depends] += " \
    npcm8xx-tip-fw:do_deploy npcm8xx-bootblock:do_deploy \
    trusted-firmware-a:do_deploy optee-os:do_deploy \
    u-boot-nuvoton:do_deploy"
do_compile() {
    # copy Openbmc built images
    cd ${DEPLOY_DIR_IMAGE}
    cp -v ${BB_BIN} bl31.bin tee.bin u-boot.bin ${IGPS_SCRIPT_BASE}/inputs

    cd ${IGPS_SCRIPT_BASE}
    install -d output_binaries/tmp
    install -d inputs/key_input
    if [ "${TIP_IMAGE}" = "True" ] || [ "${SA_TIP_IMAGE}" = "True" ];then
      # Do not sign combo0 image again
      python3 ${S}/py_scripts/GenerateAll.py openssl ${DEPLOY_DIR_IMAGE}
    else
      # for No TIP, we can run IGPS script directly
      python3 ${S}/py_scripts/GenerateAll.py openssl
    fi
}

do_deploy() {
    OUT=${IGPS_SCRIPT_BASE}/output_binaries
    BOOTLOADER=u-boot.bin.merged
    install -d ${DEPLOYDIR}
    if [ "${SA_TIP_IMAGE}" = "True" ];then
        install -m 644 ${OUT}/Secure/image_no_tip_SA.bin ${DEPLOYDIR}/${BOOTLOADER}
    elif [ "${TIP_IMAGE}" = "True" ];then
        install -m 644 ${OUT}/Secure/Kmt_TipFwL0_Skmt_TipFwL1_BootBlock_BL31_Tee_uboot.bin ${DEPLOYDIR}/${BOOTLOADER}
    else
        install -m 644 ${OUT}/Basic/image_no_tip.bin ${DEPLOYDIR}/${BOOTLOADER}
    fi
}
addtask deploy before do_build after do_compile
PACKAGE_ARCH = "${MACHINE_ARCH}"
