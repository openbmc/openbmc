require recipes-bsp/trusted-firmware-a/trusted-firmware-a.inc

# TF-A v2.10.3
SRCREV_tfa = "0f915309c3821ce6f78f8451e5a6178d0cf07611"
SRCBRANCH = "lts-v2.10"

LIC_FILES_CHKSUM += "file://docs/license.rst;md5=b2c740efedc159745b9b31f88ff03dde"

# mbedtls-3.4.1
SRC_URI_MBEDTLS = "git://github.com/ARMmbed/mbedtls.git;name=mbedtls;protocol=https;destsuffix=git/mbedtls;branch=master"
SRCREV_mbedtls = "72718dd87e087215ce9155a826ee5a66cfbe9631"

LIC_FILES_CHKSUM_MBEDTLS = "file://mbedtls/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
