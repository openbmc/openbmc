# RD V2 specific TFA support

COMPATIBLE_MACHINE = "rdv2"
TFA_PLATFORM       = "rdn2"
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

SRC_URI_TRUSTED_FIRMWARE_A = "git://gitlab.arm.com/infra-solutions/reference-design/platsw/trusted-firmware-a.git;protocol=https"
SRCBRANCH = "refinfra"
# SHA not in any branches.  so use RD-INFRA-2025.07.03
SRCREV_tfa = "a4b376b128bb5b91771002f7808566f53c8d9f3a"

# FIXME - hacking around using the 2.14 based recipe
SRC_URI:remove = "file://0001-feat-build-add-HOSTLDFLAGS-to-pass-flags-to-host-lin.patch"
LIC_FILES_CHKSUM:remove = "file://docs/license.rst;md5=6ed7bace7b0bc63021c6eba7b524039e"
LIC_FILES_CHKSUM += "file://docs/license.rst;md5=1118e32884721c0be33267bd7ae11130"
