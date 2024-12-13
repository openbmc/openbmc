DESCRIPTION = "Trusted Firmware-A"
LICENSE = "BSD-2-Clause & BSD-3-Clause & MIT & Apache-2.0"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit deploy

SRC_URI_TRUSTED_FIRMWARE_A ?= "git://review.trustedfirmware.org/TF-A/trusted-firmware-a;protocol=https"
SRCBRANCH = "master"
SRC_URI = "${SRC_URI_TRUSTED_FIRMWARE_A};name=tfa;branch=${SRCBRANCH}"

UPSTREAM_CHECK_GITTAGREGEX = "^(lts-)?v(?P<pver>\d+(\.\d+)+)$"

SRCREV_FORMAT = "tfa"

COMPATIBLE_MACHINE ?= "invalid"

# Platform must be set for each machine
TFA_PLATFORM ?= "invalid"

# Some platforms can have multiple board configurations
# Leave empty for default behavior
TFA_BOARD ?= ""

# Some platforms use SPD (Secure Payload Dispatcher) services
# Few options are "opteed", "tlkd", "trusty", "tspd", "spmd"...
# Leave empty to not use SPD
TFA_SPD ?= ""

# Variable used when TFA_SPD=spmd
TFA_SPMD_SPM_AT_SEL2 ?= "1"

# SP layout file location. Used when TFA_SPD=spmd and TFA_SPMD_SPM_AT_SEL2=1
TFA_SP_LAYOUT_FILE ?= ""

# SPMC manifest file location. Used when TFA_SPD=spmd and TFA_SPMD_SPM_AT_SEL2=1
TFA_ARM_SPMC_MANIFEST_DTS ?= ""

# Build for debug (set TFA_DEBUG to 1 to activate)
TFA_DEBUG ?= "0"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

# mbed TLS support (set TFA_MBEDTLS to 1 to activate)
TFA_MBEDTLS ?= "0"
# sub-directory in which mbedtls will be downloaded
TFA_MBEDTLS_DIR ?= "mbedtls"
# This should be set to MBEDTLS download URL if MBEDTLS is needed
SRC_URI_MBEDTLS ??= ""
# This should be set to MBEDTLS LIC FILES checksum
LIC_FILES_CHKSUM_MBEDTLS ??= ""
# add MBEDTLS to our sources if activated
SRC_URI:append = " ${@bb.utils.contains('TFA_MBEDTLS', '1', '${SRC_URI_MBEDTLS}', '', d)}"
# Update license variables
LICENSE:append = "${@bb.utils.contains('TFA_MBEDTLS', '1', ' & Apache-2.0', '', d)}"
LIC_FILES_CHKSUM:append = "${@bb.utils.contains('TFA_MBEDTLS', '1', ' ${LIC_FILES_CHKSUM_MBEDTLS}', '', d)}"
# add mbed TLS to version
SRCREV_FORMAT:append = "${@bb.utils.contains('TFA_MBEDTLS', '1', '_mbedtls', '', d)}"

# U-boot support (set TFA_UBOOT to 1 to activate)
# When U-Boot support is activated BL33 is activated with u-boot.bin file
TFA_UBOOT ??= "0"

# UEFI support (set TFA_UEFI to 1 to activate)
# When UEFI support is activated BL33 is activated with uefi.bin file
TFA_UEFI ??= "0"

# What to build
# By default we only build bl1, do_deploy will copy
# everything listed in this variable (by default bl1.bin)
TFA_BUILD_TARGET ?= "bl1"

# What to install
# do_install and do_deploy will install everything listed in this
# variable. It is set by default to TFA_BUILD_TARGET
TFA_INSTALL_TARGET ?= "${TFA_BUILD_TARGET}"

# Requires CROSS_COMPILE set by hand as there is no configure script
export CROSS_COMPILE="${TARGET_PREFIX}"

# Let the Makefile handle setting up the CFLAGS and LDFLAGS as it is a standalone application
CFLAGS[unexport] = "1"
LDFLAGS[unexport] = "1"
AS[unexport] = "1"
LD[unexport] = "1"

# No configure
do_configure[noexec] = "1"

# Baremetal, just need a compiler
DEPENDS:remove = "virtual/${TARGET_PREFIX}compilerlibs virtual/libc"

# We need dtc for dtbs compilation
# We need openssl for fiptool
DEPENDS = "dtc-native openssl-native"
DEPENDS:append:toolchain-clang = " compiler-rt"

# CC and LD introduce arguments which conflict with those otherwise provided by
# this recipe. The heads of these variables excluding those arguments
# are therefore used instead.
def remove_options_tail (in_string):
    from itertools import takewhile
    return ' '.join(takewhile(lambda x: not x.startswith('-'), in_string.split(' ')))

EXTRA_OEMAKE += "LD='${@remove_options_tail(d.getVar('LD'))}'"

EXTRA_OEMAKE += "CC='${@remove_options_tail(d.getVar('CC'))}'"

# Verbose builds, no -Werror
EXTRA_OEMAKE += "V=1 E=0"

# Add platform parameter
EXTRA_OEMAKE += "BUILD_BASE=${B} PLAT=${TFA_PLATFORM}"

# Handle TFA_BOARD parameter
EXTRA_OEMAKE += "${@'TARGET_BOARD=${TFA_BOARD}' if d.getVar('TFA_BOARD') else ''}"

# Handle TFA_SPD parameter
EXTRA_OEMAKE += "${@'SPD=${TFA_SPD}' if d.getVar('TFA_SPD') else ''}"

# If TFA_SPD is spmd, set SPMD_SPM_AT_SEL2
EXTRA_OEMAKE += "${@'SPMD_SPM_AT_SEL2=${TFA_SPMD_SPM_AT_SEL2}' if d.getVar('TFA_SPD', True) == 'spmd' else ''}"

# Handle TFA_DEBUG parameter
EXTRA_OEMAKE += "${@bb.utils.contains('TFA_DEBUG', '1', 'DEBUG=${TFA_DEBUG}', '', d)}"

# Handle MBEDTLS
EXTRA_OEMAKE += "${@bb.utils.contains('TFA_MBEDTLS', '1', 'MBEDTLS_DIR=${TFA_MBEDTLS_DIR}', '', d)}"

# Uboot support
DEPENDS += " ${@bb.utils.contains('TFA_UBOOT', '1', 'u-boot', '', d)}"
do_compile[depends] += " ${@bb.utils.contains('TFA_UBOOT', '1', 'u-boot:do_deploy', '', d)}"
EXTRA_OEMAKE += "${@bb.utils.contains('TFA_UBOOT', '1', 'BL33=${DEPLOY_DIR_IMAGE}/u-boot.bin', '', d)}"

# UEFI support
DEPENDS += " ${@bb.utils.contains('TFA_UEFI', '1', 'edk2-firmware', '', d)}"
EXTRA_OEMAKE += "${@bb.utils.contains('TFA_UEFI', '1', 'BL33=${RECIPE_SYSROOT}/firmware/uefi.bin', '', d)}"

# TFTF test support
DEPENDS += " ${@bb.utils.contains('TFTF_TESTS', '1', 'tf-a-tests', '', d)}"
EXTRA_OEMAKE += "${@bb.utils.contains('TFTF_TESTS', '1', 'BL33=${RECIPE_SYSROOT}/firmware/tftf.bin', '',d)}"

# Hafnium support
SEL2_SPMC = "${@'${TFA_SPMD_SPM_AT_SEL2}' if d.getVar('TFA_SPD', True) == 'spmd' else ''}"

DEPENDS += " ${@bb.utils.contains('SEL2_SPMC', '1', 'hafnium', '', d)}"

EXTRA_OEMAKE += "${@bb.utils.contains('SEL2_SPMC', '1', 'CTX_INCLUDE_EL2_REGS=1 ARM_ARCH_MINOR=4 BL32=${RECIPE_SYSROOT}/firmware/hafnium.bin', '', d)}"

# Add SP layout file and spmc manifest for hafnium
EXTRA_OEMAKE += "${@bb.utils.contains('SEL2_SPMC', '1', 'SP_LAYOUT_FILE=${TFA_SP_LAYOUT_FILE}' if d.getVar('TFA_SP_LAYOUT_FILE') else '', '', d)}"

EXTRA_OEMAKE += "${@bb.utils.contains('SEL2_SPMC', '1', 'ARM_SPMC_MANIFEST_DTS=${TFA_ARM_SPMC_MANIFEST_DTS}' if d.getVar('TFA_ARM_SPMC_MANIFEST_DTS') else '', '', d)}"

# Tell the tools where the native OpenSSL is located
EXTRA_OEMAKE += "OPENSSL_DIR=${STAGING_DIR_NATIVE}/${prefix_native}"
# Use the correct native compiler
EXTRA_OEMAKE += "HOSTCC='${BUILD_CC}'"

# Runtime variables
EXTRA_OEMAKE += "RUNTIME_SYSROOT=${STAGING_DIR_HOST}"

BUILD_DIR = "${B}/${TFA_PLATFORM}"
BUILD_DIR .= "${@'/${TFA_BOARD}' if d.getVar('TFA_BOARD') else ''}"
BUILD_DIR .= "/${@'debug' if d.getVar("TFA_DEBUG") == '1' else 'release'}"

do_compile() {
    # This is still needed to have the native tools executing properly by
    # setting the RPATH
    sed -i '/^LDOPTS/ s,$, \$\{BUILD_LDFLAGS},' ${S}/tools/fiptool/Makefile
    sed -i '/^INCLUDE_PATHS/ s,$, \$\{BUILD_CFLAGS},' ${S}/tools/fiptool/Makefile
    sed -i '/^LIB/ s,$, \$\{BUILD_LDFLAGS},' ${S}/tools/cert_create/Makefile

    # Currently there are races if you build all the targets at once in parallel
    for T in ${TFA_BUILD_TARGET}; do
        oe_runmake -C ${S} $T
    done
}
do_compile[cleandirs] = "${B}"

do_install() {
    install -d -m 755 ${D}/firmware
    for atfbin in ${TFA_INSTALL_TARGET}; do
        processed="0"
        if [ "$atfbin" = "all" ]; then
            # Target all is not handled by default
            bberror "all as TFA_INSTALL_TARGET is not handled by do_install"
            bberror "Please specify valid targets in TFA_INSTALL_TARGET or"
            bberror "rewrite or turn off do_install"
            exit 1
        fi

        if [ -f ${BUILD_DIR}/$atfbin.bin ]; then
            echo "Install $atfbin.bin"
            install -m 0644 ${BUILD_DIR}/$atfbin.bin \
                ${D}/firmware/$atfbin-${TFA_PLATFORM}.bin
            ln -sf $atfbin-${TFA_PLATFORM}.bin ${D}/firmware/$atfbin.bin
            processed="1"
        fi
        if [ -f ${BUILD_DIR}/$atfbin/$atfbin.elf ]; then
            echo "Install $atfbin.elf"
            install -m 0644 ${BUILD_DIR}/$atfbin/$atfbin.elf \
                ${D}/firmware/$atfbin-${TFA_PLATFORM}.elf
            ln -sf $atfbin-${TFA_PLATFORM}.elf ${D}/firmware/$atfbin.elf
            processed="1"
        fi
        if [ -f ${BUILD_DIR}/$atfbin ]; then
            echo "Install $atfbin"
            install -m 0644 ${BUILD_DIR}/$atfbin \
                ${D}/firmware/$atfbin-${TFA_PLATFORM}
            ln -sf $atfbin-${TFA_PLATFORM} ${D}/firmware/$atfbin
            processed="1"
        fi
        if [ -f ${BUILD_DIR}/fdts/$atfbin.dtb ]; then
            echo "Install $atfbin.dtb"
            install -m 0644 "${BUILD_DIR}/fdts/$atfbin.dtb" \
                "${D}/firmware/$atfbin.dtb"
            processed="1"
        elif [ "$atfbin" = "dtbs" ]; then
            echo "dtbs install, skipped: set dtbs in TFA_INSTALL_TARGET"
        elif [ -f ${B}/tools/$atfbin/$atfbin ]; then
            echo "Tools $atfbin install, skipped"
        elif [ "$processed" = "0" ]; then
            bberror "Unsupported TFA_INSTALL_TARGET target $atfbin"
            exit 1
        fi
    done
}

FILES:${PN} = "/firmware"
SYSROOT_DIRS += "/firmware"

FILES:${PN}-dbg = "/firmware/*.elf"
# Skip QA check for relocations in .text of elf binaries
INSANE_SKIP:${PN}-dbg += "textrel"
# Build paths are currently embedded
INSANE_SKIP:${PN} += "buildpaths"
INSANE_SKIP:${PN}-dbg += "buildpaths"

do_deploy() {
    cp -rf ${D}/firmware/* ${DEPLOYDIR}/
}
addtask deploy after do_install

CVE_PRODUCT = "arm:arm-trusted-firmware \
               arm:trusted_firmware-a \
               arm:arm_trusted_firmware \
               arm_trusted_firmware_project:arm_trusted_firmware"
