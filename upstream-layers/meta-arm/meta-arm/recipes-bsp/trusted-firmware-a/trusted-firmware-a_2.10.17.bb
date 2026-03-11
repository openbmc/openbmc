require recipes-bsp/trusted-firmware-a/trusted-firmware-a.inc

# TF-A v2.10.17
SRCREV_tfa = "4b1f6671b0cbddad7577fea5fcd7027f5533dc71"
SRCBRANCH = "lts-v2.10"

LIC_FILES_CHKSUM += "file://docs/license.rst;md5=b2c740efedc159745b9b31f88ff03dde"

# in TF-A src, docs/getting_started/prerequisites.rst lists the expected version mbedtls
# mbedtls-3.6.3
SRCBRANCH_MBEDTLS = "mbedtls-3.6"
SRC_URI_MBEDTLS = "git://github.com/ARMmbed/mbedtls.git;name=mbedtls;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/mbedtls;branch=${SRCBRANCH_MBEDTLS}"
SRCREV_mbedtls = "22098d41c6620ce07cf8a0134d37302355e1e5ef"

LIC_FILES_CHKSUM_MBEDTLS = "file://mbedtls/LICENSE;md5=379d5819937a6c2f1ef1630d341e026d"

# continue to boot also without TPM
SRC_URI += "\
    file://0001-qemu_measured_boot.c-ignore-TPM-error-and-continue-w.patch \
    file://0001-fix-zynqmp-handle-secure-SGI-at-EL1-for-OP-TEE.patch \
"
