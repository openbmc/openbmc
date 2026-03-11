require recipes-bsp/trusted-firmware-a/trusted-firmware-a.inc

# TF-A v2.13.0
SRCREV_tfa = "c17351450c8a513ca3f30f936e26a71db693a145"
SRCBRANCH = "master"

LIC_FILES_CHKSUM += "file://docs/license.rst;md5=1118e32884721c0be33267bd7ae11130"

# in TF-A src, docs/getting_started/prerequisites.rst lists the expected version mbedtls
# mbedtls-3.6.3
SRCBRANCH_MBEDTLS = "mbedtls-3.6"
SRC_URI_MBEDTLS = "git://github.com/ARMmbed/mbedtls.git;name=mbedtls;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/mbedtls;branch=${SRCBRANCH_MBEDTLS}"
SRCREV_mbedtls = "22098d41c6620ce07cf8a0134d37302355e1e5ef"

LIC_FILES_CHKSUM_MBEDTLS = "file://mbedtls/LICENSE;md5=379d5819937a6c2f1ef1630d341e026d"
