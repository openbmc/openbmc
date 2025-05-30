require recipes-bsp/trusted-firmware-a/trusted-firmware-a.inc

# TF-A v2.12.1
SRCREV_tfa = "8cf9edba5cc3ec11ed5463f206aa5819f7fdbade"
SRCBRANCH = "lts-v2.12"

LIC_FILES_CHKSUM += "file://docs/license.rst;md5=83b7626b8c7a37263c6a58af8d19bee1"

# in TF-A src, docs/getting_started/prerequisites.rst lists the expected version mbedtls
# mbedtls-3.6.2
SRCBRANCH_MBEDTLS = "mbedtls-3.6"
SRC_URI_MBEDTLS = "git://github.com/ARMmbed/mbedtls.git;name=mbedtls;protocol=https;destsuffix=git/mbedtls;branch=${SRCBRANCH_MBEDTLS}"
SRCREV_mbedtls = "107ea89daaefb9867ea9121002fbbdf926780e98"

LIC_FILES_CHKSUM_MBEDTLS = "file://mbedtls/LICENSE;md5=379d5819937a6c2f1ef1630d341e026d"

# continue to boot also without TPM
SRC_URI += "\
    file://0001-qemu_measured_boot.c-ignore-TPM-error-and-continue-w.patch \
"
