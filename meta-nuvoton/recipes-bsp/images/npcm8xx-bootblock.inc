SUMMARY = "Primary bootloader for NPCM8XX (Arbel) devices"
DESCRIPTION = "Primary bootloader for NPCM8XX (Arbel) devices"
HOMEPAGE = "https://github.com/Nuvoton-Israel/npcm8xx-bootblock"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/git"
B = "${S}/Sources"
BB_BRANCH ?= "main"
SRC_URI = " \
    git://github.com/Nuvoton-Israel/npcm8xx-bootblock;branch=${BB_BRANCH};protocol=https"

export CROSS_COMPILE="${TARGET_PREFIX}"
CFLAGS[unexport] = "1"
LDFLAGS[unexport] = "1"
AS[unexport] = "1"
LD[unexport] = "1"
do_configure[noexec] = "1"
OPTIONAL_FLAGS = ""

EXTRA_OEMAKE += "CROSS_COMPILER_INC=${STAGING_DIR_HOST}${includedir} OPTIONAL_FLAGS=${OPTIONAL_FLAGS}"

TIP = "${@'tip' if d.getVar("TIP_IMAGE") == 'True' else 'no_tip'}"
BOOTBLOCK = "arbel_a35_bootblock"
BOOTBLOCK .= "${@'_no_tip' if d.getVar("TIP_IMAGE") != 'True' else ''}"

do_compile() {
    oe_runmake "${BOOTBLOCK}" ENCLAVE="${TIP}"
}
do_compile[cleandirs] = "${B}/Images"

inherit deploy
do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 644 "${B}/Images/${TIP}/${BOOTBLOCK}.bin" "${DEPLOYDIR}/${BOOTBLOCK}.bin"
}

addtask deploy before do_build after do_compile
