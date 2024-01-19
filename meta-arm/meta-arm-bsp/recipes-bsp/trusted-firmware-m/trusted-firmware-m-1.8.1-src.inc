# Common src definitions for trusted-firmware-m and trusted-firmware-m-scripts

LICENSE = "BSD-2-Clause & BSD-3-Clause & Apache-2.0"

LIC_FILES_CHKSUM = "file://license.rst;md5=07f368487da347f3c7bd0fc3085f3afa \
                    file://../tf-m-tests/license.rst;md5=4481bae2221b0cfca76a69fb3411f390 \
                    file://../mbedtls/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://../mcuboot/LICENSE;md5=b6ee33f1d12a5e6ee3de1e82fb51eeb8"

SRC_URI_TRUSTED_FIRMWARE_M ?= "git://git.trustedfirmware.org/TF-M/trusted-firmware-m.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_TESTS ?= "git://git.trustedfirmware.org/TF-M/tf-m-tests.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_MBEDTLS ?= "git://github.com/ARMmbed/mbedtls.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_MCUBOOT ?= "git://github.com/mcu-tools/mcuboot.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_QCBOR ?= "git://github.com/laurencelundblade/QCBOR.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_EXTRAS ?= "git://git.trustedfirmware.org/TF-M/tf-m-extras.git;protocol=https"
SRC_URI  = "${SRC_URI_TRUSTED_FIRMWARE_M};branch=${SRCBRANCH_tfm};name=tfm;destsuffix=git/tfm \
            ${SRC_URI_TRUSTED_FIRMWARE_M_TESTS};branch=${SRCBRANCH_tfm-tests};name=tfm-tests;destsuffix=git/tf-m-tests \
            ${SRC_URI_TRUSTED_FIRMWARE_M_MBEDTLS};branch=${SRCBRANCH_mbedtls};name=mbedtls;destsuffix=git/mbedtls \
            ${SRC_URI_TRUSTED_FIRMWARE_M_MCUBOOT};branch=${SRCBRANCH_mcuboot};name=mcuboot;destsuffix=git/mcuboot \
            ${SRC_URI_TRUSTED_FIRMWARE_M_QCBOR};branch=${SRCBRANCH_qcbor};name=qcbor;destsuffix=git/qcbor \
            ${SRC_URI_TRUSTED_FIRMWARE_M_EXTRAS};branch=${SRCBRANCH_tfm-extras};name=tfm-extras;destsuffix=git/tfm-extras \
            "

# The required dependencies are documented in tf-m/config/config_default.cmake
# TF-Mv1.8.1
SRCBRANCH_tfm ?= "master"
SRCREV_tfm = "53aa78efef274b9e46e63b429078ae1863609728"
# TF-Mv1.8.1
SRCBRANCH_tfm-tests ?= "master"
SRCREV_tfm-tests = "1273c5bcd3d8ade60d51524797e0b22b6fd7eea1"
# mbedtls-3.4.1
SRCBRANCH_mbedtls ?= "master"
SRCREV_mbedtls = "72718dd87e087215ce9155a826ee5a66cfbe9631"
# mcuboot v1.10.0
SRCBRANCH_mcuboot ?= "main"
SRCREV_mcuboot = "23d28832f02dcdc18687782c6cd8ba99e9b274d2"
# QCBOR v1.2
SRCBRANCH_qcbor ?= "master"
SRCREV_qcbor = "b0e7033268e88c9f27146fa9a1415ef4c19ebaff"
# TF-Mv1.8.1
SRCBRANCH_tfm-extras ?= "master"
SRCREV_tfm-extras = "504ae9a9a50981e9dd4d8accec8261a1dba9e965"

SRCREV_FORMAT = "tfm"

S = "${WORKDIR}/git/tfm"
