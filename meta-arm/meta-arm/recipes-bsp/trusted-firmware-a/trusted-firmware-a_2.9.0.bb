require trusted-firmware-a.inc

# TF-A v2.9.0
SRCREV_tfa = "d3e71ead6ea5bc3555ac90a446efec84ef6c6122"

# Enable passing TOS_FW_CONFIG from FIP package to Trusted OS.
SRC_URI:append:qemuarm64-secureboot = " \
            file://0001-Add-spmc_manifest-for-qemu.patch \
        "

LIC_FILES_CHKSUM += "file://docs/license.rst;md5=b2c740efedc159745b9b31f88ff03dde"

# mbedtls-3.4.0
SRC_URI_MBEDTLS = "git://github.com/ARMmbed/mbedtls.git;name=mbedtls;protocol=https;destsuffix=git/mbedtls;branch=master"
SRCREV_mbedtls = "1873d3bfc2da771672bd8e7e8f41f57e0af77f33"

LIC_FILES_CHKSUM_MBEDTLS = "file://mbedtls/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
