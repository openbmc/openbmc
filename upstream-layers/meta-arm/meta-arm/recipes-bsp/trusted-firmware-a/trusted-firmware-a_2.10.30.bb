require recipes-bsp/trusted-firmware-a/trusted-firmware-a.inc

# TF-A v2.10.30
SRCREV_tfa = "d57b81079003e1647ed4181057c5784c7e3b1c3e"
SRCBRANCH = "lts-v2.10"

LIC_FILES_CHKSUM += "file://docs/license.rst;md5=b2c740efedc159745b9b31f88ff03dde"

# in TF-A src, docs/getting_started/prerequisites.rst lists the expected version mbedtls
# mbedtls-3.6.4
SRCBRANCH_MBEDTLS = "mbedtls-3.6"
SRC_URI_MBEDTLS = "git://github.com/Mbed-TLS/mbedtls;name=mbedtls;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/mbedtls;branch=${SRCBRANCH_MBEDTLS}"
SRCREV_mbedtls = "c765c831e5c2a0971410692f92f7a81d6ec65ec2"

LIC_FILES_CHKSUM_MBEDTLS = "file://mbedtls/LICENSE;md5=379d5819937a6c2f1ef1630d341e026d"

# continue to boot also without TPM
SRC_URI += "\
    file://0001-qemu_measured_boot.c-ignore-TPM-error-and-continue-w.patch \
    file://0001-fix-zynqmp-handle-secure-SGI-at-EL1-for-OP-TEE.patch \
"
