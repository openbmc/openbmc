# Corstone1000 machines specific TFM support

COMPATIBLE_MACHINE = "(corstone1000)"

TFM_PLATFORM = "arm/corstone1000"

TFM_DEBUG = "1"

## Default is the MPS3 board
TFM_PLATFORM_IS_FVP ?= "FALSE"
EXTRA_OECMAKE += "-DPLATFORM_IS_FVP=${TFM_PLATFORM_IS_FVP}"
EXTRA_OECMAKE += "-DCC312_LEGACY_DRIVER_API_ENABLED=ON"

SRCREV_tfm = "94c55967cbd1832681f07074a0945605b02ec8d0"
SRCREV_mcuboot = "9e8eddcecba931f99297765779f8b130d808a9a3"
SRCREV_mbedtls = "8c89224991adff88d53cd380f42a2baa36f91454"

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


SRC_URI:remove:corstone1000 =" \
    file://rwx.patch    \
"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append:corstone1000 = " \
    file://0001-Platform-corstone1000-make-sure-to-write-fwu-metadata-to-repl.patch      \
    file://0002-Platform-corstone1000-get-fwu-and-private-metadata-f.patch      \
    file://0003-Platform-corstone1000-Add-watchdog_reset_timer.patch            \
    file://0004-Platform-corstone1000-Replace-MCUBOOT-BL1-by-TFM-s.patch        \
    file://0005-Platform-corstone1000-Replace-MCUBOOT-BL1-by-TFM-s-B.patch      \
    file://0006-Platform-corstone1000-Reorganize-bl2-files.patch                \
    file://0007-Platform-corstone1000-Fix-linker-script-comment.patch           \
    file://0008-Platform-corstone1000-Fix-linkerscripts-copyright-ye.patch      \
    file://0009-Platform-corstone1000-fix-flash-reading-issue-for-fi.patch      \
    file://0010-Platform-corstone1000-Adds-compiler-flags-to-FWU-age.patch      \
    file://0011-Platform-corstone1000-adjust-PS-asset-configuration.patch       \
    file://0012-Platform-corstone1000-Increase-number-of-assets.patch           \
    file://corstone1000/rwx.patch                                               \
    "

# TF-M ships patches for external dependencies that needs to be applied
apply_tfm_patches() {
    find ${S}/lib/ext/qcbor -type f -name '*.patch' -print0 | sort -z | xargs -r -t -0 -n 1 patch -p1 -d ${S}/../qcbor/ -i
    find ${S}/lib/ext/mbedcrypto -type f -name '*.patch' -print0 | sort -z | xargs -r -t -0 -n 1 patch -p1 -d ${S}/../mbedtls/ -i
    find ${S}/lib/ext/mcuboot -type f -name '*.patch' -print0 | sort -z | xargs -r -t -0 -n 1 patch -p1 -d ${S}/../mcuboot/ -i
    find ${S}/lib/ext/tf-m-tests -type f -name '*.patch' -print0 | sort -z | xargs -r -t -0 -n 1 patch -p1 -d ${S}/../tf-m-tests/ -i
}

do_patch[postfuncs] += "apply_tfm_patches"

do_install() {
  install -D -p -m 0644 ${B}/install/outputs/tfm_s_signed.bin ${D}/firmware/tfm_s_signed.bin
  install -D -p -m 0644 ${B}/install/outputs/bl2_signed.bin ${D}/firmware/bl2_signed.bin
  install -D -p -m 0644 ${B}/install/outputs/bl1_1.bin ${D}/firmware/bl1_1.bin
  install -D -p -m 0644 ${B}/install/outputs/bl1_provisioning_bundle.bin ${D}/firmware/bl1_provisioning_bundle.bin
}

create_bl1_image(){
    dd conv=notrunc bs=1 if=${D}/firmware/bl1_1.bin of=${D}/firmware/bl1.bin seek=0
    dd conv=notrunc bs=1 if=${D}/firmware/bl1_provisioning_bundle.bin of=${D}/firmware/bl1.bin seek=40960
}
do_install[postfuncs] += "create_bl1_image"
