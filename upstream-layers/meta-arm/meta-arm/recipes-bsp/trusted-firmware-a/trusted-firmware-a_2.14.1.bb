require recipes-bsp/trusted-firmware-a/trusted-firmware-a.inc

# TF-A v2.14.1
SRC_URI_TRUSTED_FIRMWARE_A = "gitsm://review.trustedfirmware.org/TF-A/trusted-firmware-a;protocol=https"
SRCREV_tfa = "e82c7ced9e76aea35b176e608d67dfe5ebe1c569"
SRCBRANCH = "lts-v2.14"

LIC_FILES_CHKSUM += "file://docs/license.rst;md5=6ed7bace7b0bc63021c6eba7b524039e"

# in TF-A src, docs/getting_started/prerequisites.rst lists the expected version mbedtls
# mbedtls-3.6.5
SRCBRANCH_MBEDTLS = "mbedtls-3.6"
SRC_URI_MBEDTLS = "gitsm://github.com/Mbed-TLS/mbedtls;name=mbedtls;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/mbedtls;branch=${SRCBRANCH_MBEDTLS}"
SRCREV_mbedtls = "e185d7fd85499c8ce5ca2a54f5cf8fe7dbe3f8df"

LIC_FILES_CHKSUM_MBEDTLS = "file://mbedtls/LICENSE;md5=379d5819937a6c2f1ef1630d341e026d"

SRC_URI += "file://0001-feat-build-add-HOSTLDFLAGS-to-pass-flags-to-host-lin.patch"
