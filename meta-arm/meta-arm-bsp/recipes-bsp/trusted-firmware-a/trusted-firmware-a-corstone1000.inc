# Corstone1000 64-bit machines specific TFA support

COMPATIBLE_MACHINE = "(corstone1000)"

FILESEXTRAPATHS:prepend := "${THISDIR}/files/corstone1000:"
SRC_URI:append = " \
	file://0001-Fix-FF-A-version-in-SPMC-manifest.patch \
	file://0002-fix-corstone1000-pass-spsr-value-explicitly.patch \
	file://0003-fix-corstone1000-remove-unused-NS_SHARED_RAM-region.patch \
	file://0004-fix-corstone1000-clean-the-cache-and-disable-interru.patch \
	file://0005-feat-corstone1000-Add-multicore-support-for-FVP-plat.patch \
	file://0006-feat-corstone1000-include-platform-header-file.patch \
       "

TFA_DEBUG = "1"
TFA_UBOOT ?= "1"
TFA_MBEDTLS = "1"
TFA_BUILD_TARGET = "bl2 bl31 fip"

# Enabling Secure-EL1 Payload Dispatcher (SPD)
TFA_SPD = "spmd"
# Cortex-A35 supports Armv8.0-A (no S-EL2 execution state).
# So, the SPD SPMC component should run at the S-EL1 execution state
TFA_SPMD_SPM_AT_SEL2 = "0"

# BL2 loads BL32 (optee). So, optee needs to be built first:
DEPENDS += "optee-os"

# Note: Regarding the build option: LOG_LEVEL.
# There seems to be an issue when setting it
# to 50 (LOG_LEVEL_VERBOSE), where the kernel
# tee driver sends yielding requests to OP-TEE
# at a faster pace than OP-TEE processes them,
# as the processing time is consumed by logging
# in TF-A. When this issue occurs, booting halts
# as soon as optee driver starts initialization.
# Therefore, it's not currently recommended to
# set LOG_LEVEL to 50 at all.
EXTRA_OEMAKE:append = " \
                        ARCH=aarch64 \
                        TARGET_PLATFORM=${TFA_TARGET_PLATFORM} \
                        ENABLE_STACK_PROTECTOR=strong \
                        ENABLE_PIE=1 \
                        RESET_TO_BL2=1 \
                        CREATE_KEYS=1 \
                        GENERATE_COT=1 \
                        TRUSTED_BOARD_BOOT=1 \
                        ARM_GPT_SUPPORT=1 \
                        PSA_FWU_SUPPORT=1 \
                        NR_OF_IMAGES_IN_FW_BANK=4 \
                        COT=tbbr \
                        ARM_ROTPK_LOCATION=devel_rsa  \
                        ERRATA_A35_855472=1 \
                        ROT_KEY=plat/arm/board/common/rotpk/arm_rotprivk_rsa.pem \
                        BL32=${RECIPE_SYSROOT}/${nonarch_base_libdir}/firmware/tee-pager_v2.bin \
                        FVP_USE_GIC_DRIVER=FVP_GICV2 \
                        "
EXTRA_OEMAKE:append:corstone1000-fvp = "${@bb.utils.contains('MACHINE_FEATURES', 'corstone1000_fvp_smp', ' ENABLE_MULTICORE=1', '', d)}"
