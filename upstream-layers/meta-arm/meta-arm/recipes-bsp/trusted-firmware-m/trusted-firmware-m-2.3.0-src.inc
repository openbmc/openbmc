# Common src definitions for trusted-firmware-m and trusted-firmware-m-scripts

LICENSE = "BSD-2-Clause & BSD-3-Clause & Apache-2.0"

LIC_FILES_CHKSUM = "file://license.rst;md5=002a64dec64b82c58a203a94bee7b2a1 \
                    file://external/tf-m-tests/license.rst;md5=4481bae2221b0cfca76a69fb3411f390 \
                    file://external/mcuboot/LICENSE;md5=b6ee33f1d12a5e6ee3de1e82fb51eeb8 \
                    file://external/tfm-psa-adac/license.rst;md5=07f368487da347f3c7bd0fc3085f3afa \
                    file://external/tf-psa-crypto/LICENSE;md5=40b928d464bb11f233c578de64d710cf \
                    file://external/psa-crypto-driver/license.rst;md5=6a076b245abfcdb53243394a49e51ffa \
                    file://external/t_cose/LICENSE;md5=b2ebdbfb82602b97aa628f64cf4b65ad \
                   "

SRC_URI_TRUSTED_FIRMWARE_M ?= "git://git.trustedfirmware.org/TF-M/trusted-firmware-m.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_EXTRAS ?= "git://git.trustedfirmware.org/TF-M/tf-m-extras.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_TESTS ?= "git://git.trustedfirmware.org/TF-M/tf-m-tests.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_CMSIS ?= "git://github.com/ARM-software/CMSIS_6.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_MCUBOOT ?= "git://github.com/mcu-tools/mcuboot.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_QCBOR ?= "git://github.com/laurencelundblade/QCBOR.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_PSA_ADAC ?= "git://git.trustedfirmware.org/shared/psa-adac.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_PSA_CRYPTO ?= "gitsm://github.com/Mbed-TLS/TF-PSA-Crypto.git;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_PSA_CRYPTO_DRIVER ?= "git://git.trustedfirmware.org/shared/tf-psa-crypto-drivers;protocol=https"
SRC_URI_TRUSTED_FIRMWARE_M_T_COSE ?= "git://github.com/laurencelundblade/t_cose.git;protocol=https"

# The required dependencies are documented in tf-m/config/config_base.cmake
# TF-Mv2.3.0
SRCBRANCH_tfm ?= "release/2.3.x"
SRCREV_tfm = "5d906d29d7b6d1a7f7134960d228f9e75f6a8a07"
# TF-Mv2.3.0
SRCBRANCH_tfm-extras ?= "release/2.3.x"
SRCREV_tfm-extras = "8abeb6610e7ca27fff54dd8a9b5767d5cf98b998"
# TF-Mv2.3.0
SRCBRANCH_tfm-tests ?= "release/2.3.x"
SRCREV_tfm-tests = "6a1165dfef219d0801487f51e06d12331e726643"
# CMSIS v6.1.0, CMSIS_TAG from lib/ext/cmsis/CMakeLists.txt
SRCBRANCH_cmsis ?= "main"
SRCREV_cmsis = "b0bbb0423b278ca632cfe1474eb227961d835fd2"
# mcuboot v2.4.0, value from MCUBOOT_VERSION
SRCBRANCH_mcuboot ?= "main"
SRCREV_mcuboot = "6d3b3d2c38ab20c242e5b9abb04d050086383eb2"
# QCBOR v1.2, value from QCBOR_VERSION in lib/ext/qcbor/CMakeLists.txt
SRCBRANCH_qcbor ?= "master"
SRCREV_qcbor = "92d3f89030baff4af7be8396c563e6c8ef263622"
# PSA-ADAC (intermediate SHA), value from PLATFORM_PSA_ADAC_VERSION
SRCBRANCH_tfm-psa-adac = "master"
SRCREV_tfm-psa-adac = "eff89e8e0ce36e4793f78309be19fcfab798f473"
# TF-PSA-Crypto v1.1.0, value from TF_PSA_CRYPTO_VERSION
SRCBRANCH_tf-psa-crypto = "development"
SRCREV_tf-psa-crypto = "29160dd877d29658279fd683b2ae57b320ddcf09"
# From platform/ext/target/arm/drivers/cc3xx/CMakeLists.txt
SRCBRANCH_psa-crypto-driver = "main"
SRCREV_psa-crypto-driver = "3a93566c04c4d77a007e6b149e7c06e7b8f0cd8d"
# T_COSE v2.0-alpha-2, from lib/ext/t_cose/CMakeLists.txt
SRCBRANCH_t-cose = "dev"
SRCREV_t-cose = "3076010eeb6383f0827bd992c75b68af9311cf1d"


SRC_URI  = "${SRC_URI_TRUSTED_FIRMWARE_M};branch=${SRCBRANCH_tfm};name=tfm;destsuffix=tfm \
            ${SRC_URI_TRUSTED_FIRMWARE_M_EXTRAS};branch=${SRCBRANCH_tfm-extras};name=tfm-extras;destsuffix=tfm/external/tfm-extras \
            ${SRC_URI_TRUSTED_FIRMWARE_M_TESTS};branch=${SRCBRANCH_tfm-tests};name=tfm-tests;destsuffix=tfm/external/tf-m-tests \
            ${SRC_URI_TRUSTED_FIRMWARE_M_CMSIS};branch=${SRCBRANCH_cmsis};name=cmsis;destsuffix=tfm/external/cmsis \
            ${SRC_URI_TRUSTED_FIRMWARE_M_MCUBOOT};branch=${SRCBRANCH_mcuboot};name=mcuboot;destsuffix=tfm/external/mcuboot \
            ${SRC_URI_TRUSTED_FIRMWARE_M_QCBOR};branch=${SRCBRANCH_qcbor};name=qcbor;destsuffix=tfm/external/qcbor \
            ${SRC_URI_TRUSTED_FIRMWARE_M_PSA_ADAC};branch=${SRCBRANCH_tfm-psa-adac};name=tfm-psa-adac;destsuffix=tfm/external/tfm-psa-adac \
            ${SRC_URI_TRUSTED_FIRMWARE_M_PSA_CRYPTO};branch=${SRCBRANCH_tf-psa-crypto};name=tf-psa-crypto;destsuffix=tfm/external/tf-psa-crypto \
            ${SRC_URI_TRUSTED_FIRMWARE_M_PSA_CRYPTO_DRIVER};branch=${SRCBRANCH_psa-crypto-driver};name=psa-crypto-driver;destsuffix=tfm/external/psa-crypto-driver \
            ${SRC_URI_TRUSTED_FIRMWARE_M_T_COSE};branch=${SRCBRANCH_t-cose};name=t-cose;destsuffix=tfm/external/t_cose \
            "

SRCREV_FORMAT = "tfm_tfm-extras_tfm-tests_cmsis_mcuboot_qcbor_tfm-psa-adac_tf-psa-crypto_psa-pcrypto-driver_t-cose"

S = "${UNPACKDIR}/tfm"

EXTRA_OECMAKE += "-DTF_PSA_CRYPTO_PATH=${S}/external/tf-psa-crypto"
EXTRA_OECMAKE += "-DPSA_CRYPTO_DRIVER_PATH=${S}/external/psa-crypto-driver"

# Apply patches
inherit apply_local_src_patches
LOCAL_SRC_PATCHES_INPUT_DIR = "N/A"

do_apply_local_src_patches() {
    apply_local_src_patches ${S}/lib/ext/qcbor ${S}/external/qcbor
    apply_local_src_patches ${S}/lib/ext/mcuboot ${S}/external/mcuboot
    apply_local_src_patches ${S}/lib/ext/tf-m-tests ${S}/external/tf-m-tests
    apply_local_src_patches ${S}/lib/ext/tf-psa-crypto ${S}/external/tf-psa-crypto
    apply_local_src_patches ${S}/lib/ext/t_cose ${S}/external/t_cose
}
