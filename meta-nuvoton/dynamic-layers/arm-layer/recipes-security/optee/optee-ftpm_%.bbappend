FILESEXTRAPATHS:append:npcm8xx := "${THISDIR}/${PN}:"

SRC_URI = "gitsm://github.com/microsoft/MSRSec.git;branch=master;protocol=https \
           file://0001-Mark-TA-discoverable.patch \
           "

SRCREV = "81abeb9fa968340438b4b0c08aa6685833f0bfa1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=27e94c0280987ab296b0b8dd02ab9fe5"

EXTRA_OEMAKE:append:npcm8xx = " \
    TA_CROSS_COMPILE=${TARGET_PREFIX} \
    AR=${TARGET_PREFIX}ar \
    RANLIB=${TARGET_PREFIX}ranlib \
    RC=${TARGET_PREFIX}rc \
    TA_CPU=cortex-a35+crypto \
    CFG_TEE_CORE_DEBUG=n \
    CFG_TEE_CORE_LOG_LEVEL=0 \
    CFG_TEE_CORE_TA_TRACE=0 \
    CFG_TA_DEBUG=n \
    CFG_TEE_TA_LOG_LEVEL=0 \
    CFG_ARM64_ta_arm64=y \
    CFG_FTPM_USE_WOLF=n \
    CFG_AUTHVARS_USE_WOLF=n \
    "

BUILD_DIR = "TAs/optee_ta"
BUILD_ONLY_FTPM = "ftpm"
TA_UID = "bc50d971-d4c9-42c4-82cb-343fb7f37896"

do_compile:npcm8xx() {
    BUILD_PATH=${B}/${BUILD_DIR}
    bbnote 'Building fTPM TA'
    oe_runmake $BUILD_ONLY_FTPM --directory=$BUILD_PATH
}

do_install:npcm8xx() {
    mkdir -p ${D}/${nonarch_base_libdir}/optee_armtz
    OUTPUT_DIR=${B}/${BUILD_DIR}/out/fTPM
    install -D -p -m 0644 $OUTPUT_DIR/${TA_UID}.stripped.elf ${D}/${nonarch_base_libdir}/optee_armtz/
    install -D -p -m 0644 $OUTPUT_DIR/${TA_UID}.ta ${D}/${nonarch_base_libdir}/optee_armtz/
}

do_deploy:npcm8xx() {
    OUTPUT_DIR=${B}/${BUILD_DIR}/out/fTPM
    install -d ${DEPLOYDIR}/optee
    install -D -p -m 0644 $OUTPUT_DIR/${TA_UID}.stripped.elf  ${DEPLOYDIR}/optee/
}
