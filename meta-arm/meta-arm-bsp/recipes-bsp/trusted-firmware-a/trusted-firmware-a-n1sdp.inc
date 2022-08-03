# N1SDP specific TFA support

COMPATIBLE_MACHINE = "n1sdp"
TFA_PLATFORM       = "n1sdp"
TFA_BUILD_TARGET   = "all fip"
TFA_INSTALL_TARGET = "bl1 bl2 bl31 n1sdp-multi-chip n1sdp-single-chip n1sdp_fw_config n1sdp_tb_fw_config fip"
TFA_DEBUG          = "1"
TFA_MBEDTLS        = "1"
TFA_UBOOT          = "0"
TFA_UEFI           = "1"

SRC_URI:append = " file://bl_size.patch"

TFA_ROT_KEY= "plat/arm/board/common/rotpk/arm_rotprivk_rsa.pem"

EXTRA_OEMAKE:append = "\
                    TRUSTED_BOARD_BOOT=1 \
                    GENERATE_COT=1 \
                    CREATE_KEYS=1 \
                    ENABLE_PIE=0 \
                    ARM_ROTPK_LOCATION="devel_rsa" \
                    ROT_KEY="${TFA_ROT_KEY}" \
                    BL33=${RECIPE_SYSROOT}/firmware/uefi.bin \
                    "
