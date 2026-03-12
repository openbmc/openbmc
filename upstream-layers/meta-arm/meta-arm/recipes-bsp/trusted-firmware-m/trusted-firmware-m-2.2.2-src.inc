# Common src definitions for trusted-firmware-m and trusted-firmware-m-scripts

LICENSE = "BSD-2-Clause & BSD-3-Clause & Apache-2.0"

LIC_FILES_CHKSUM = "file://license.rst;md5=07f368487da347f3c7bd0fc3085f3afa \
                    file://external/tf-m-tests/license.rst;md5=4481bae2221b0cfca76a69fb3411f390 \
                    file://external/mbedtls/LICENSE;md5=379d5819937a6c2f1ef1630d341e026d \
                    file://external/mcuboot/LICENSE;md5=b6ee33f1d12a5e6ee3de1e82fb51eeb8 \
                    file://external/tfm-psa-adac/license.rst;md5=07f368487da347f3c7bd0fc3085f3afa \
                    file://external/t_cose/LICENSE;md5=b2ebdbfb82602b97aa628f64cf4b65ad \
                   "

SRC_URI_TRUSTED_FIRMWARE_M ?= "git://git.trustedfirmware.org/TF-M/trusted-firmware-m.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_EXTRAS ?= "git://git.trustedfirmware.org/TF-M/tf-m-extras.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_TESTS ?= "git://git.trustedfirmware.org/TF-M/tf-m-tests.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_CMSIS ?= "git://github.com/ARM-software/CMSIS_6.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_MBEDTLS ?= "gitsm://github.com/Mbed-TLS/mbedtls;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_MCUBOOT ?= "git://github.com/mcu-tools/mcuboot.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_QCBOR ?= "git://github.com/laurencelundblade/QCBOR.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_PSA_ADAC ?= "git://git.trustedfirmware.org/shared/psa-adac.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_T_COSE ?= "git://github.com/laurencelundblade/t_cose.git;protocol=https"

# The required dependencies are documented in tf-m/config/config_base.cmake
# TF-Mv2.2.2
SRCBRANCH_tfm ?= "release/2.2.x"
SRCREV_tfm = "816058200a647f988676ee4b0655e80af3601200"
# TF-Mv2.2.2
SRCBRANCH_tfm-extras ?= "release/2.2.x"
SRCREV_tfm-extras = "88a4bd39284bb497dd8b362f20cd2b7f704ac390"
# TF-Mv2.2.2
SRCBRANCH_tfm-tests ?= "release/2.2.x"
SRCREV_tfm-tests = "e0a433c67c37138cd9dce23657ae82c5cbdcf51f"
# CMSIS v6.1.0, CMSIS_TAG from lib/ext/cmsis/CMakeLists.txt
SRCBRANCH_cmsis ?= "main"
SRCREV_cmsis = "b0bbb0423b278ca632cfe1474eb227961d835fd2"
# mbedtls-3.6.5, value from MBEDCRYPTO_VERSION(-ish)
SRCBRANCH_mbedtls ?= "mbedtls-3.6"
SRCREV_mbedtls = "e185d7fd85499c8ce5ca2a54f5cf8fe7dbe3f8df"
# mcuboot v2.2.0, value from MCUBOOT_VERSION
SRCBRANCH_mcuboot ?= "main"
SRCREV_mcuboot = "2d61c318933819a0f4954fb2a5a957a62c6128ce"
# QCBOR v1.2, value from QCBOR_VERSION in lib/ext/qcbor/CMakeLists.txt
SRCBRANCH_qcbor ?= "master"
SRCREV_qcbor = "92d3f89030baff4af7be8396c563e6c8ef263622"
# PSA-ADAC (intermediate SHA), value from PLATFORM_PSA_ADAC_VERSION
SRCBRANCH_tfm-psa-adac = "master"
SRCREV_tfm-psa-adac = "819a254af6fb5eefdcef194ec85d2c7627451351"
# T_COSE v2.0-alpha-2, from lib/ext/t_cose/CMakeLists.txt
SRCBRANCH_t-cose = "dev"
SRCREV_t-cose = "3076010eeb6383f0827bd992c75b68af9311cf1d"


SRC_URI  = "${SRC_URI_TRUSTED_FIRMWARE_M};branch=${SRCBRANCH_tfm};name=tfm;destsuffix=tfm \
            ${SRC_URI_TRUSTED_FIRMWARE_M_EXTRAS};branch=${SRCBRANCH_tfm-extras};name=tfm-extras;destsuffix=tfm/external/tfm-extras \
            ${SRC_URI_TRUSTED_FIRMWARE_M_TESTS};branch=${SRCBRANCH_tfm-tests};name=tfm-tests;destsuffix=tfm/external/tf-m-tests \
            ${SRC_URI_TRUSTED_FIRMWARE_M_CMSIS};branch=${SRCBRANCH_cmsis};name=cmsis;destsuffix=tfm/external/cmsis \
            ${SRC_URI_TRUSTED_FIRMWARE_M_MBEDTLS};branch=${SRCBRANCH_mbedtls};name=mbedtls;destsuffix=tfm/external/mbedtls \
            ${SRC_URI_TRUSTED_FIRMWARE_M_MCUBOOT};branch=${SRCBRANCH_mcuboot};name=mcuboot;destsuffix=tfm/external/mcuboot \
            ${SRC_URI_TRUSTED_FIRMWARE_M_QCBOR};branch=${SRCBRANCH_qcbor};name=qcbor;destsuffix=tfm/external/qcbor \
            ${SRC_URI_TRUSTED_FIRMWARE_M_PSA_ADAC};branch=${SRCBRANCH_tfm-psa-adac};name=tfm-psa-adac;destsuffix=tfm/external/tfm-psa-adac \
            ${SRC_URI_TRUSTED_FIRMWARE_M_T_COSE};branch=${SRCBRANCH_t-cose};name=t-cose;destsuffix=tfm/external/t_cose \
            "

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
    apply_local_src_patches ${S}/lib/ext/t_cose ${S}/external/t_cose
}
