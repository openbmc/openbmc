# N1SDP specific TFA support

# Align with N1SDP-2023.06.22 Manifest
SRCREV_tfa  = "31f60a968347497562b0129134928d7ac4767710"
PV .= "+git"

COMPATIBLE_MACHINE = "n1sdp"
TFA_BUILD_TARGET   = "all fip"
TFA_INSTALL_TARGET = "bl1 bl2 bl31 n1sdp-multi-chip n1sdp-single-chip n1sdp_fw_config n1sdp_tb_fw_config fip"
TFA_DEBUG          = "1"
TFA_MBEDTLS        = "1"
TFA_UBOOT          = "0"
TFA_UEFI          ?= "1"

FILESEXTRAPATHS:prepend := "${THISDIR}/files/n1sdp:"

SRC_URI:append = " \
    file://0001-Reserve-OP-TEE-memory-from-nwd.patch \
    file://0002-Modify-BL32-Location-to-DDR4.patch \
    file://0003-Modify-SPMC-Base-to-DDR4.patch \
    "

TFA_ROT_KEY= "plat/arm/board/common/rotpk/arm_rotprivk_rsa.pem"

# Enabling Secure-EL1 Payload Dispatcher (SPD)
TFA_SPD = "spmd"
# Cortex-A35 supports Armv8.0-A (no S-EL2 execution state).
# So, the SPD SPMC component should run at the S-EL1 execution state
TFA_SPMD_SPM_AT_SEL2 = "0"

# BL2 loads BL32 (optee). So, optee needs to be built first:
DEPENDS += "optee-os"

EXTRA_OEMAKE:append = "\
                    TRUSTED_BOARD_BOOT=1 \
		    GENERATE_COT=1 \
		    CREATE_KEYS=1 \
		    ARM_ROTPK_LOCATION="devel_rsa" \
		    ROT_KEY="${TFA_ROT_KEY}" \
		    BL32=${RECIPE_SYSROOT}/${nonarch_base_libdir}/firmware/tee-pager_v2.bin \
		    "
