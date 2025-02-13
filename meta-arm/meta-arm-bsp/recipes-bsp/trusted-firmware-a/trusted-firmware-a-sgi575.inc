# SGI575 specific TFA support

COMPATIBLE_MACHINE = "sgi575"
TFA_PLATFORM       = "sgi575"
TFA_BUILD_TARGET   = "all fip"
TFA_INSTALL_TARGET = "bl1 fip"
TFA_DEBUG          = "1"
TFA_MBEDTLS        = "1"
TFA_UBOOT          = "0"
TFA_UEFI           = "1"

EXTRA_OEMAKE += "TRUSTED_BOARD_BOOT=1 GENERATE_COT=1 ARM_ROTPK_LOCATION=devel_rsa \
                     ROT_KEY=plat/arm/board/common/rotpk/arm_rotprivk_rsa.pem"

# If GENERATE_COT is set, then tf-a will try to use local poetry install
# to run the python cot-dt2c command.  Disable the local poetry and use
# the provided cot-dt2c.
EXTRA_OEMAKE += "POETRY=''"
DEPENDS += "cot-dt2c-native"
