# Common src definitions for trusted-firmware-m and trusted-firmware-m-scripts

LICENSE = "BSD-2-Clause & BSD-3-Clause & Apache-2.0"

LIC_FILES_CHKSUM = "file://license.rst;md5=07f368487da347f3c7bd0fc3085f3afa \
                    file://../tf-m-tests/license.rst;md5=4481bae2221b0cfca76a69fb3411f390 \
                    file://../mbedtls/LICENSE;md5=379d5819937a6c2f1ef1630d341e026d \
                    file://../mcuboot/LICENSE;md5=b6ee33f1d12a5e6ee3de1e82fb51eeb8"

SRC_URI_TRUSTED_FIRMWARE_M ?= "git://git.trustedfirmware.org/TF-M/trusted-firmware-m.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_EXTRAS ?= "git://git.trustedfirmware.org/TF-M/tf-m-extras.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_TESTS ?= "git://git.trustedfirmware.org/TF-M/tf-m-tests.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_MBEDTLS ?= "git://github.com/ARMmbed/mbedtls.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_MCUBOOT ?= "git://github.com/mcu-tools/mcuboot.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_QCBOR ?= "git://github.com/laurencelundblade/QCBOR.git;protocol=https"
SRC_URI  = "${SRC_URI_TRUSTED_FIRMWARE_M};branch=${SRCBRANCH_tfm};name=tfm;destsuffix=git/tfm \
            ${SRC_URI_TRUSTED_FIRMWARE_M_EXTRAS};branch=${SRCBRANCH_tfm-extras};name=tfm-extras;destsuffix=git/tfm-extras \
            ${SRC_URI_TRUSTED_FIRMWARE_M_TESTS};branch=${SRCBRANCH_tfm-tests};name=tfm-tests;destsuffix=git/tf-m-tests \
            ${SRC_URI_TRUSTED_FIRMWARE_M_MBEDTLS};branch=${SRCBRANCH_mbedtls};name=mbedtls;destsuffix=git/mbedtls \
            ${SRC_URI_TRUSTED_FIRMWARE_M_MCUBOOT};branch=${SRCBRANCH_mcuboot};name=mcuboot;destsuffix=git/mcuboot \
            ${SRC_URI_TRUSTED_FIRMWARE_M_QCBOR};branch=${SRCBRANCH_qcbor};name=qcbor;destsuffix=git/qcbor \
            "

# The required dependencies are documented in tf-m/config/config_default.cmake
# TF-Mv2.0.0
SRCBRANCH_tfm ?= "release/2.0.x"
SRCREV_tfm = "9ca8a5eb3c85eecee1303dffa262800ea0385584"
# TF-Mv2.0.0
SRCBRANCH_tfm-extras ?= "release/2.0.x"
SRCREV_tfm-extras = "676a1465f361439bc95f5a50ef71749f27caffc1"
# TF-Mv2.0.0
SRCBRANCH_tfm-tests ?= "release/2.0.x"
SRCREV_tfm-tests = "69fbb233dc6e45f8306d98694ca5760559f9d2ef"
# mbedtls-3.5.1
SRCBRANCH_mbedtls ?= "master"
SRCREV_mbedtls = "edb8fec9882084344a314368ac7fd957a187519c"
# mcuboot v2.0.0
SRCBRANCH_mcuboot ?= "main"
SRCREV_mcuboot = "304fd41980ed929533b9f387dde1b463b0be5b90"
# QCBOR v1.2
SRCBRANCH_qcbor ?= "master"
SRCREV_qcbor = "b0e7033268e88c9f27146fa9a1415ef4c19ebaff"

SRCREV_FORMAT = "tfm"

S = "${WORKDIR}/git/tfm"
