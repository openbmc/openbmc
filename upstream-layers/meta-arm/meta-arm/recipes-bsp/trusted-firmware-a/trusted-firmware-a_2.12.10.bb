require recipes-bsp/trusted-firmware-a/trusted-firmware-a.inc

# TF-A v2.12.10
SRCREV_tfa = "9487b105dcf18d057c6aeb405f9eaa1079c8fe6e"
SRCBRANCH = "lts-v2.12"

LIC_FILES_CHKSUM += "file://docs/license.rst;md5=83b7626b8c7a37263c6a58af8d19bee1"

# in TF-A src, docs/getting_started/prerequisites.rst lists the expected version mbedtls
# mbedtls-3.6.3
SRCBRANCH_MBEDTLS = "mbedtls-3.6"
SRC_URI_MBEDTLS = "git://github.com/Mbed-TLS/mbedtls;name=mbedtls;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/mbedtls;branch=${SRCBRANCH_MBEDTLS}"
SRCREV_mbedtls = "22098d41c6620ce07cf8a0134d37302355e1e5ef"

LIC_FILES_CHKSUM_MBEDTLS = "file://mbedtls/LICENSE;md5=379d5819937a6c2f1ef1630d341e026d"

# continue to boot also without TPM
SRC_URI += "\
    file://0001-qemu_measured_boot.c-ignore-TPM-error-and-continue-w.patch \
"
