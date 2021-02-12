# Class to test UBOOT_MKIMAGE and UBOOT_MKIMAGE_SIGN
# (in conjunction with kernel-fitimage.bbclass)
#
# SPDX-License-Identifier: MIT
#

UBOOT_MKIMAGE = "test_mkimage_wrapper"
UBOOT_MKIMAGE_SIGN = "test_mkimage_signing_wrapper"

test_mkimage_wrapper() {
    echo "### uboot-mkimage wrapper message"
    uboot-mkimage "$@"
}

test_mkimage_signing_wrapper() {
    echo "### uboot-mkimage signing wrapper message"
    uboot-mkimage "$@"
}

