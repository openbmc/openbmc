# Corstone1000 machines specific TFM support

COMPATIBLE_MACHINE = "(corstone1000)"

TFM_PLATFORM = "arm/corstone1000"

TFM_DEBUG = "1"

## Default is the MPS3 board
TFM_PLATFORM_IS_FVP ?= "FALSE"
EXTRA_OECMAKE += "-DPLATFORM_IS_FVP=${TFM_PLATFORM_IS_FVP}"
EXTRA_OECMAKE += "-DCC312_LEGACY_DRIVER_API_ENABLED=OFF"
EXTRA_OECMAKE:append:corstone1000-fvp = " -DENABLE_MULTICORE=${@bb.utils.contains('MACHINE_FEATURES', 'corstone1000_fvp_smp', 'TRUE', 'FALSE', d)}"

SRC_URI += " \
            file://0001-arm-trusted-firmware-m-disable-address-warnings-into.patch \
           "

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append:corstone1000 = " \
    file://0001-platform-corstone1000-Update-MPU-configuration.patch \
    file://0002-platform-corstone1000-Cover-S_DATA-with-MPU.patch \
    file://0003-platform-corstone1000-align-capsule-update-structs.patch \
    file://0004-Platform-Corstone1000-skip-the-first-nv-counter.patch \
    file://0005-platform-corstone1000-add-unique-guid-for-mps3.patch \
    file://0006-Platform-Corstone1000-Enable-host-firewall-in-FVP.patch \
    file://0007-platform-corstone1000-Increase-ITS-max-asset-size.patch \
    file://0008-Platform-CS1000-Replace-OpenAMP-with-RSE_COMMS.patch \
    file://0009-platform-corstone1000-Increase-RSE_COMMS-buffer-size.patch \
    file://0010-CC312-alignment-of-cc312-differences-between-fvp-and.patch \
    file://0011-Platform-corstone1000-Increase-buffers-for-EFI-vars.patch \
    file://0012-corstone1000-Remove-reset-after-capsule-update.patch \
    file://0013-platform-CS1000-Add-multicore-support-for-FVP.patch \
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
