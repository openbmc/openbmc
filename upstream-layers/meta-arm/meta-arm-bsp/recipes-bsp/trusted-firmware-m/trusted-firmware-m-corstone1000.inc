# Corstone1000 machines specific TFM support

COMPATIBLE_MACHINE = "(corstone1000)"

TFM_PLATFORM = "arm/corstone1000"

# FIXME - debug shouldn't be used here, as it should be used when doing bring up or actual debug.  However, switching this to the release version causes issues with TFM related to 
# sbrk.c:(.text._sbrk+0x18): undefined reference to `end'
# which is being caused by heap not being enabled.. So, just use the size optimization to keep this small enough to fit in to flash
TFM_DEBUG = "1"
EXTRA_OECMAKE:append = " -DCMAKE_C_FLAGS_DEBUG='-Os -g'"

# These dependencies are needed for TF-M v2.2.0 and above
# https://github.com/TrustedFirmware-M/trusted-firmware-m/blob/TF-Mv2.2.0/tools/requirements.txt
DEPENDS:append = " clang-native python3-rich-native python3-pyelftools-native"

## Default is the MPS3 board
TFM_PLATFORM_IS_FVP ?= "FALSE"

## Default is Cortex-A35
## Cortex-A320 is implemented inside a DynamIQ Shared Unit-120T (DSU-120T) cluster
EXTRA_OECMAKE:append:cortexa320 = " \
    -DCORSTONE1000_CORTEX_A320=TRUE \
    -DCORSTONE1000_DSU_120T=TRUE \
"

EXTRA_OECMAKE += "-DPLATFORM_IS_FVP=${TFM_PLATFORM_IS_FVP}"
EXTRA_OECMAKE += "-DCC312_LEGACY_DRIVER_API_ENABLED=OFF"
EXTRA_OECMAKE:append = " -DENABLE_MULTICORE=${@bb.utils.contains('MACHINE_FEATURES', 'corstone1000_smp', 'TRUE', 'FALSE', d)}"
EXTRA_OECMAKE:append:corstone1000-mps3 = " -DPLATFORM_PSA_ADAC_SECURE_DEBUG=${@bb.utils.contains('MACHINE_FEATURES', 'secure-debug', 'ON', 'OFF', d)}"
# Keep the device in CM until authenticated DCU_EN can be preserved across
# reset and applied from BL2.
EXTRA_OECMAKE:append:corstone1000-mps3 = " -DCORSTONE1000_SKIP_PROVISIONING_FOR_DEBUG=${@bb.utils.contains('MACHINE_FEATURES', 'secure-debug', 'ON', 'OFF', d)}"
EXTRA_OECMAKE:append:corstone1000-mps3 = " -DPLATFORM_PSA_ADAC_SOURCE_PATH=${S}/external/tfm-psa-adac -DPLATFORM_PSA_ADAC_BUILD_PATH=${B}/tfm-psa-adac-build"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append:corstone1000 = " \
    file://0001-CC312-alignment-of-cc312-differences-between-fvp-and.patch \
    file://0002-Corstone-1000-Enable-different-DRBG-configurations.patch \
    file://0003-bl2-corstone-1000-secure-debug-waiting-in-CM-LCS.patch \
    file://0004-Build-fix-remaining-GCC-v14.2-AES-type-error.patch \
    file://0005-plat-cs1k-Removed-unused-variables.patch \
    file://0006-lib-gpt-Show-intent-of-GUIDs-in-unittests-more-clear.patch \
    file://0007-lib-gpt-Provide-macro-identifying-free-space.patch \
    file://0008-lib-gpt-Add-operation-to-duplicate-entries.patch \
    file://0009-lib-gpt-Consecutively-erase-blocks-when-moving-parti.patch \
    file://0010-lib-gpt-Clarify-API-operation.patch \
    file://0011-lib-gpt-Add-metadata-only-API-operations.patch \
    file://0012-plat-cs1k-Add-flash-erase-protection.patch \
    file://0013-platform-corstone1000-Increase-FIP-partition-size.patch \
    file://0014-platform-corstone1000-keep-CM-during-secure-debug.patch \
    "

FILESEXTRAPATHS:prepend:corstone1000-mps3 := "${THISDIR}/files/corstone1000/psa-adac:"
SRC_URI:append:corstone1000-mps3 = " \
    file://0001-PSA-revert-header-versions.patch;patchdir=external/tfm-psa-adac \
    file://0002-cmake-Update-psa_adac_psa_crypto-dependencies.patch;patchdir=external/tfm-psa-adac \
    "

create_bl1_image(){
    dd conv=notrunc bs=1 if=${D}${FIRMWARE_DIR}/bl1_1.bin of=${D}${FIRMWARE_DIR}/bl1.bin seek=0
    # Size of bl1_1.bin is 58KB (59208 bytes)
    dd conv=notrunc bs=1 if=${D}${FIRMWARE_DIR}/bl1_provisioning_bundle.bin of=${D}${FIRMWARE_DIR}/bl1.bin seek=59208
}
do_install[postfuncs] += "create_bl1_image"
