# Common src definitions for trusted-firmware-m and trusted-firmware-m-scripts

LICENSE = "BSD-2-Clause & BSD-3-Clause & Apache-2.0"

LIC_FILES_CHKSUM = "file://license.rst;md5=07f368487da347f3c7bd0fc3085f3afa \
                    file://../tf-m-tests/license.rst;md5=02d06ffb8d9f099ff4961c0cb0183a18 \
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
# TF-Mv1.7.0
SRCBRANCH_tfm ?= "master"
SRCREV_tfm = "b725a1346cdb9ec75b1adcdc4c84705881e8fd4e"
# TF-Mv1.7.0
SRCBRANCH_tfm-tests ?= "master"
SRCREV_tfm-tests = "4c4b58041c6c01670266690538a780b4a23d08b8"
# mbedtls-3.2.1
SRCBRANCH_mbedtls ?= "master"
SRCREV_mbedtls = "869298bffeea13b205343361b7a7daf2b210e33d"
# v1.9.0
SRCBRANCH_mcuboot ?= "main"
SRCREV_mcuboot = "c657cbea75f2bb1faf1fceacf972a0537a8d26dd"
# qcbor
SRCBRANCH_qcbor ?= "master"
SRCREV_qcbor = "b0e7033268e88c9f27146fa9a1415ef4c19ebaff"
# TF-Mv1.7.0
SRCBRANCH_tfm-extras ?= "master"
SRCREV_tfm-extras = "daacaa6df3881e205bc03d75fc8fb688afe9f0f1"

SRCREV_FORMAT = "tfm"

S = "${WORKDIR}/git/tfm"
