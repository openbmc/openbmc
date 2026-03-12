# Common src definitions for trusted-firmware-m and trusted-firmware-m-scripts

LICENSE = "BSD-2-Clause & BSD-3-Clause & Apache-2.0"

LIC_FILES_CHKSUM = "file://license.rst;md5=07f368487da347f3c7bd0fc3085f3afa \
                    file://external/tf-m-tests/license.rst;md5=4481bae2221b0cfca76a69fb3411f390 \
                    file://external/mbedtls/LICENSE;md5=379d5819937a6c2f1ef1630d341e026d \
                    file://external/mcuboot/LICENSE;md5=b6ee33f1d12a5e6ee3de1e82fb51eeb8 \
                    file://external/tfm-psa-adac/license.rst;md5=07f368487da347f3c7bd0fc3085f3afa"

SRC_URI_TRUSTED_FIRMWARE_M ?= "git://git.trustedfirmware.org/TF-M/trusted-firmware-m.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_EXTRAS ?= "git://git.trustedfirmware.org/TF-M/tf-m-extras.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_TESTS ?= "git://git.trustedfirmware.org/TF-M/tf-m-tests.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_CMSIS ?= "git://github.com/ARM-software/CMSIS_6.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_MBEDTLS ?= "gitsm://github.com/Mbed-TLS/mbedtls;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_MCUBOOT ?= "git://github.com/mcu-tools/mcuboot.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_QCBOR ?= "git://github.com/laurencelundblade/QCBOR.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_PSA_ADAC ?= "git://git.trustedfirmware.org/shared/psa-adac.git;protocol=https"
SRC_URI  = "${SRC_URI_TRUSTED_FIRMWARE_M};branch=${SRCBRANCH_tfm};name=tfm;destsuffix=tfm \
            ${SRC_URI_TRUSTED_FIRMWARE_M_EXTRAS};branch=${SRCBRANCH_tfm-extras};name=tfm-extras;destsuffix=tfm/external/tfm-extras \
            ${SRC_URI_TRUSTED_FIRMWARE_M_TESTS};branch=${SRCBRANCH_tfm-tests};name=tfm-tests;destsuffix=tfm/external/tf-m-tests \
            ${SRC_URI_TRUSTED_FIRMWARE_M_CMSIS};branch=${SRCBRANCH_cmsis};name=cmsis;destsuffix=tfm/external/cmsis \
            ${SRC_URI_TRUSTED_FIRMWARE_M_MBEDTLS};branch=${SRCBRANCH_mbedtls};name=mbedtls;destsuffix=tfm/external/mbedtls \
            ${SRC_URI_TRUSTED_FIRMWARE_M_MCUBOOT};branch=${SRCBRANCH_mcuboot};name=mcuboot;destsuffix=tfm/external/mcuboot \
            ${SRC_URI_TRUSTED_FIRMWARE_M_QCBOR};branch=${SRCBRANCH_qcbor};name=qcbor;destsuffix=tfm/external/qcbor \
            ${SRC_URI_TRUSTED_FIRMWARE_M_PSA_ADAC};branch=${SRCBRANCH_tfm-psa-adac};name=tfm-psa-adac;destsuffix=tfm/external/tfm-psa-adac \
            "

# The required dependencies are documented in tf-m/config/config_base.cmake
# TF-Mv2.1.4
SRCBRANCH_tfm ?= "release/2.1.x"
SRCREV_tfm = "03d02d6feed02583c41acfe7147d1117678797a3"
# TF-Mv2.1.4
SRCBRANCH_tfm-extras ?= "release/2.1.x"
SRCREV_tfm-extras = "ef7814a8c95d7df4b150a66557c0073cddce6593"
# TF-Mv2.1.4
SRCBRANCH_tfm-tests ?= "release/2.1.x"
SRCREV_tfm-tests = "852c6f74c5b7ae61e140d1a46e981f95177aa40e"
# CMSIS v6.0.0+ (intermediate SHA), CMSIS_TAG from lib/ext/cmsis/CMakeLists.txt
SRCBRANCH_cmsis ?= "main"
SRCREV_cmsis = "d0c460c1697d210b49a4b90998195831c0cd325c"
# mbedtls-3.6.5, value from MBEDCRYPTO_VERSION
SRCBRANCH_mbedtls ?= "mbedtls-3.6"
SRCREV_mbedtls = "e185d7fd85499c8ce5ca2a54f5cf8fe7dbe3f8df"
# mcuboot v2.1.0, value from MCUBOOT_VERSION
SRCBRANCH_mcuboot ?= "main"
SRCREV_mcuboot = "9c99326b9756dbcc35b524636d99ed5f3e6cb29b"
# QCBOR v1.2, value from QCBOR_VERSION in lib/ext/qcbor/CMakeLists.txt
SRCBRANCH_qcbor ?= "master"
SRCREV_qcbor = "b0e7033268e88c9f27146fa9a1415ef4c19ebaff"
# PSA-ADAC (intermediate SHA), value from PLATFORM_PSA_ADAC_VERSION
SRCBRANCH_tfm-psa-adac = "master"
SRCREV_tfm-psa-adac = "5f5490cebe66ae997f316f83c3fbf1f97deef625"

SRCREV_FORMAT = "tfm_tfm-extras_tfm-tests_cmsis_mbedtls_mcuboot_qcbor_tfm-psa-adac_t-cose"

S = "${UNPACKDIR}/tfm"

# Apply patches
inherit apply_local_src_patches
LOCAL_SRC_PATCHES_INPUT_DIR = "N/A"

do_apply_local_src_patches() {
    apply_local_src_patches ${S}/lib/ext/qcbor ${S}/external/qcbor
    apply_local_src_patches ${S}/lib/ext/mbedcrypto ${S}/external/mbedtls
    apply_local_src_patches ${S}/lib/ext/mcuboot ${S}/external/mcuboot
    apply_local_src_patches ${S}/lib/ext/tf-m-tests ${S}/external/tf-m-tests
}
