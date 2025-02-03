DESCRIPTION = "Trusted Firmware-A tests(aka TFTF)"
LICENSE = "BSD-3-Clause & NCSA"

LIC_FILES_CHKSUM += "file://docs/license.rst;md5=6175cc0aa2e63b6d21a32aa0ee7d1b4a"

inherit deploy

COMPATIBLE_MACHINE ?= "invalid"

SRC_URI_TRUSTED_FIRMWARE_A_TESTS ?= "git://git.trustedfirmware.org/TF-A/tf-a-tests.git;protocol=https"
SRC_URI = "${SRC_URI_TRUSTED_FIRMWARE_A_TESTS};nobranch=1"
SRCBRANCH = "master"
SRCREV = "8917cf8b5eeb409b63256076d0dc35c60930ce18"

DEPENDS += "optee-os"

EXTRA_OEMAKE += "USE_NVM=0"
EXTRA_OEMAKE += "SHELL_COLOR=1"
EXTRA_OEMAKE += "DEBUG=1"

# Modify mode based on debug or release mode
TFTF_MODE ?= "debug"

# Platform must be set for each machine
TFA_PLATFORM ?= "invalid"

EXTRA_OEMAKE += "ARCH=aarch64"
EXTRA_OEMAKE += "LOG_LEVEL=50"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

# Add platform parameter
EXTRA_OEMAKE += "BUILD_BASE=${B} PLAT=${TFA_PLATFORM}"

# Requires CROSS_COMPILE set by hand as there is no configure script
export CROSS_COMPILE="${TARGET_PREFIX}"

LDFLAGS[unexport] = "1"
do_compile() {
    oe_runmake -C ${S} tftf
}

do_compile[cleandirs] = "${B}"

FILES:${PN} = "/firmware/tftf.bin"
SYSROOT_DIRS += "/firmware"

do_install() {
    install -d -m 755 ${D}/firmware
    install -m 0644 ${B}/${TFA_PLATFORM}/${TFTF_MODE}/tftf.bin ${D}/firmware/tftf.bin
}

do_deploy() {
    cp -rf ${D}/firmware/* ${DEPLOYDIR}/
}
addtask deploy after do_install
