# Corstone1000 machines specific TFM support

COMPATIBLE_MACHINE = "(corstone1000)"

TFM_PLATFORM = "arm/corstone1000"

TFM_DEBUG = "1"

## Default is the MPS3 board
TFM_PLATFORM_IS_FVP ?= "FALSE"
EXTRA_OECMAKE += "-DPLATFORM_IS_FVP=${TFM_PLATFORM_IS_FVP}"
EXTRA_OECMAKE += "-DCC312_LEGACY_DRIVER_API_ENABLED=OFF"

# libmetal v2023.04.0
LICENSE += "& BSD-3-Clause"
LIC_FILES_CHKSUM += "file://../libmetal/LICENSE.md;md5=f4d5df0f12dcea1b1a0124219c0dbab4"
SRC_URI += "git://github.com/OpenAMP/libmetal.git;protocol=https;branch=main;name=libmetal;destsuffix=git/libmetal \
            file://0001-cmake-modify-path-to-libmetal-version-file.patch;patchdir=../libmetal \
            file://0002-arm-trusted-firmware-m-disable-address-warnings-into.patch \
           "
SRCREV_libmetal = "28fa2351d6a8121ce6c1c2ac5ee43ce08d38dbae"
EXTRA_OECMAKE += "-DLIBMETAL_SRC_PATH=${S}/../libmetal -DLIBMETAL_BIN_PATH=${B}/libmetal-build"
# The configuration can fail if libmetal tries to generate the docs and the doxygen bin is found
EXTRA_OECMAKE += "-DWITH_DOC=False"

# OpenAMP v2023.04.0
LICENSE += "& BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM += "file://../openamp/LICENSE.md;md5=ab88daf995c0bd0071c2e1e55f3d3505"
SRC_URI += "git://github.com/OpenAMP/open-amp.git;protocol=https;branch=main;name=openamp;destsuffix=git/openamp"
SRCREV_openamp = "accac4d3610cbb268f3c3fe3c31dc45dd4c4dd17"
EXTRA_OECMAKE += "-DLIBOPENAMP_SRC_PATH=${S}/../openamp -DLIBOPENAMP_BIN_PATH=${B}/libopenamp-build"


FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append:corstone1000 = " \
    file://0001-platform-corstone1000-Update-MPU-configuration.patch \
    file://0002-platform-corstone1000-Cover-S_DATA-with-MPU.patch \
    file://0003-Platform-corstone1000-Fix-issues-due-to-adjustment-M.patch \
    file://0004-platform-corstone1000-align-capsule-update-structs.patch \
    file://0005-platform-corstone1000-fix-synchronization-issue-on-o.patch \
    file://0006-Platform-Corstone1000-skip-the-first-nv-counter.patch \
    file://0007-platform-corstone1000-add-unique-guid-for-mps3.patch \    
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
  install -D -p -m 0644 ${B}/bin/tfm_s_signed.bin ${D}/firmware/tfm_s_signed.bin
  install -D -p -m 0644 ${B}/bin/bl2_signed.bin ${D}/firmware/bl2_signed.bin
  install -D -p -m 0644 ${B}/bin/bl1_1.bin ${D}/firmware/bl1_1.bin
  install -D -p -m 0644 ${B}/bin/bl1_provisioning_bundle.bin ${D}/firmware/bl1_provisioning_bundle.bin
}

create_bl1_image(){
    dd conv=notrunc bs=1 if=${D}/firmware/bl1_1.bin of=${D}/firmware/bl1.bin seek=0
    dd conv=notrunc bs=1 if=${D}/firmware/bl1_provisioning_bundle.bin of=${D}/firmware/bl1.bin seek=40960
}
do_install[postfuncs] += "create_bl1_image"
