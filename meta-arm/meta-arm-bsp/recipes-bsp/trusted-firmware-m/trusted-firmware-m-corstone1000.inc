# Corstone1000 machines specific TFM support

COMPATIBLE_MACHINE = "(corstone1000)"

TFM_PLATFORM = "arm/corstone1000"

TFM_DEBUG = "1"

## Default is the MPS3 board
TFM_PLATFORM_IS_FVP ?= "FALSE"
EXTRA_OECMAKE += "-DPLATFORM_IS_FVP=${TFM_PLATFORM_IS_FVP}"
EXTRA_OECMAKE += "-DCC312_LEGACY_DRIVER_API_ENABLED=OFF"

SRCBRANCH_tfm = "master"
SRC_URI  = "git://git.trustedfirmware.org/TF-M/trusted-firmware-m.git;protocol=https;branch=${SRCBRANCH_tfm};name=tfm;destsuffix=git/tfm \
            git://git.trustedfirmware.org/TF-M/tf-m-tests.git;protocol=https;branch=master;name=tfm-tests;destsuffix=git/tf-m-tests \
            git://github.com/ARMmbed/mbedtls.git;protocol=https;branch=master;name=mbedtls;destsuffix=git/mbedtls \
            git://github.com/mcu-tools/mcuboot.git;protocol=https;branch=main;name=mcuboot;destsuffix=git/mcuboot \
            "

# Bumping the SHA of TFM is required as multiple changes are needed in the TFM to support the ESRT 
# The most crucial change needed is TFM support for UEFI FMP Image Information 
SRCREV_tfm = "b065a6b28cc6c692b99e4f7e9387d96f51bf4d07"
SRCREV_mbedtls = "869298bffeea13b205343361b7a7daf2b210e33d"
SRCREV_mcuboot = "c657cbea75f2bb1faf1fceacf972a0537a8d26dd"
SRCREV_tfm-tests = "3e6c52b4b255e4b1343ba6a257a77fa7a976e8fb"
PV .= "+git${SRCPV}"
SRCREV_FORMAT ?= "tfm"

# libmetal
LICENSE += "& BSD-3-Clause"
LIC_FILES_CHKSUM += "file://../libmetal/LICENSE.md;md5=fe0b8a4beea8f0813b606d15a3df3d3c"
SRC_URI += "git://github.com/OpenAMP/libmetal.git;protocol=https;branch=main;name=libmetal;destsuffix=git/libmetal"
SRCREV_libmetal = "f252f0e007fbfb8b3a52b1d5901250ddac96baad"
EXTRA_OECMAKE += "-DLIBMETAL_SRC_PATH=${S}/../libmetal -DLIBMETAL_BIN_PATH=${B}/libmetal-build"

# OpenAMP
LICENSE += "& BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM += "file://../openamp/LICENSE.md;md5=a8d8cf662ef6bf9936a1e1413585ecbf"
SRC_URI += "git://github.com/OpenAMP/open-amp.git;protocol=https;branch=main;name=openamp;destsuffix=git/openamp"
SRCREV_openamp = "347397decaa43372fc4d00f965640ebde042966d"
EXTRA_OECMAKE += "-DLIBOPENAMP_SRC_PATH=${S}/../openamp -DLIBOPENAMP_BIN_PATH=${B}/libopenamp-build"

# Secure Debug ADAC
LICENSE += "& BSD-3-Clause"
LIC_FILES_CHKSUM += "file://../psa-adac/license.rst;md5=07f368487da347f3c7bd0fc3085f3afa"
SRC_URI += "git://git.trustedfirmware.org/shared/psa-adac.git;protocol=https;branch=master;name=psa-adac;destsuffix=git/psa-adac"
SRCREV_psa-adac = "427923cc0152578d536fb2065154d5d0dd874910"
# Secure debug is disabled by default
EXTRA_OECMAKE += "-DPLATFORM_PSA_ADAC_SECURE_DEBUG=OFF"
EXTRA_OECMAKE += "-DPLATFORM_PSA_ADAC_SOURCE_PATH=${S}/../psa-adac -DPLATFORM_PSA_ADAC_BUILD_PATH=${B}/libpsa-adac-build"

DEPENDS += "trusted-firmware-a"

do_install() {
  install -D -p -m 0644 ${B}/install/outputs/tfm_s_signed.bin ${D}/firmware/tfm_s_signed.bin
  install -D -p -m 0644 ${B}/install/outputs/bl2_signed.bin ${D}/firmware/bl2_signed.bin
  install -D -p -m 0644 ${B}/install/outputs/bl1.bin ${D}/firmware/bl1.bin
}
