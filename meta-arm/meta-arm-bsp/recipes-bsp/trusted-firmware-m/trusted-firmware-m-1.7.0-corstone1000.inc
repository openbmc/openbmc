# Corstone1000 machines specific TFM support

COMPATIBLE_MACHINE = "(corstone1000)"

TFM_PLATFORM = "arm/corstone1000"

TFM_DEBUG = "1"

## Default is the MPS3 board
TFM_PLATFORM_IS_FVP ?= "FALSE"
EXTRA_OECMAKE += "-DPLATFORM_IS_FVP=${TFM_PLATFORM_IS_FVP}"
EXTRA_OECMAKE += "-DCC312_LEGACY_DRIVER_API_ENABLED=OFF"

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


FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append:corstone1000 = " \
    file://0001-Platform-corstone1000-Introduce-IO-framework.patch              \
    file://0002-Platform-corstone1000-Add-IO-test-in-ci_regressions.patch       \
    file://0003-Platform-corstone1000-Add-soft-crc32-calculation.patch          \
    file://0004-Platform-corstone1000-calculate-metadata-crc32.patch            \
    file://0005-Platform-corstone1000-fwu-metadata_read-validate-crc.patch      \
    file://0006-Platform-corstone1000-Add-common-platform-logger.patch          \
    file://0007-Platform-corstone1000-Introduce-GPT-parser.patch                \
    file://0008-Platform-corstone1000-BL1-changes-to-adapt-to-new-fl.patch      \
    file://0009-Platform-corstone1000-BL2-uses-GPT-layout.patch                 \
    file://0010-Platform-corstone1000-flash_layout-simplification.patch         \
    file://0011-corstone1000-make-sure-to-write-fwu-metadata-to-repl.patch      \
           "

do_install() {
  install -D -p -m 0644 ${B}/install/outputs/tfm_s_signed.bin ${D}/firmware/tfm_s_signed.bin
  install -D -p -m 0644 ${B}/install/outputs/bl2_signed.bin ${D}/firmware/bl2_signed.bin
  install -D -p -m 0644 ${B}/install/outputs/bl1.bin ${D}/firmware/bl1.bin
}
