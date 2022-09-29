require trusted-firmware-a.inc

# TF-A v2.7
SRCREV_tfa = "35f4c7295bafeb32c8bcbdfb6a3f2e74a57e732b"

SRC_URI += "file://rwx-segments.patch"

LIC_FILES_CHKSUM += "file://docs/license.rst;md5=b2c740efedc159745b9b31f88ff03dde"

# mbed TLS v2.28.0
SRC_URI_MBEDTLS = "git://github.com/ARMmbed/mbedtls.git;name=mbedtls;protocol=https;destsuffix=git/mbedtls;branch=mbedtls-2.28"
SRCREV_mbedtls = "8b3f26a5ac38d4fdccbc5c5366229f3e01dafcc0"

LIC_FILES_CHKSUM_MBEDTLS = "file://mbedtls/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
