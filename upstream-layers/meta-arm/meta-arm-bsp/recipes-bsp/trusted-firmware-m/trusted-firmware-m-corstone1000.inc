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
EXTRA_OECMAKE:append:corstone1000-fvp = " -DENABLE_MULTICORE=${@bb.utils.contains('MACHINE_FEATURES', 'corstone1000_fvp_smp', 'TRUE', 'FALSE', d)}"
EXTRA_OECMAKE:append:corstone1000-mps3 = " -DPLATFORM_PSA_ADAC_SECURE_DEBUG=${@bb.utils.contains('MACHINE_FEATURES', 'secure-debug', 'ON', 'OFF', d)}"
EXTRA_OECMAKE:append:corstone1000-mps3 = " -DPLATFORM_PSA_ADAC_SOURCE_PATH=${S}/external/tfm-psa-adac -DPLATFORM_PSA_ADAC_BUILD_PATH=${B}/tfm-psa-adac-build"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append:corstone1000 = " \
    file://0001-arm-trusted-firmware-m-disable-address-warnings-into.patch \
    file://0002-Platform-CS1000-Remove-unused-BL1-files.patch \
    file://0003-Platform-Corstone1000-Fix-BL1-compiler-switch-and-re.patch \
    file://0004-CC312-alignment-of-cc312-differences-between-fvp-and.patch \
    file://0005-Platform-Corstone1000-Enable-FWU-partition.patch \
    file://0006-Platform-Corstone1000-Implement-Bootloader-Abstracti.patch \
    file://0007-Platform-Corstone1000-Increase-buffer-sizes.patch \
    file://0008-Platform-Corstone1000-Remove-duplicate-configuration.patch \
    file://0009-plat-corstone1000-Add-support-for-Cortex-A320-varian.patch \
    file://0010-Corstone-1000-Enable-different-DRBG-configurations.patch \
    file://0011-bl2-corstone-1000-Remove-psa_adac_to_tfm_apply_permi.patch \
    file://0012-bl2-corstone-1000-secure-debug-waiting-in-CM-LCS.patch \
    file://0013-Build-adjust-CS1000-platform-for-GCC-v14.2.patch \
    file://0014-Workaround-compile-errors-in-AES.patch \
    "

SRCREV_tfm-psa-adac:corstone1000 = "f2809ae231be33a1afcd7714f40756c67d846c88"
FILESEXTRAPATHS:prepend:corstone1000-mps3 := "${THISDIR}/files/corstone1000/psa-adac:"
SRC_URI:append:corstone1000-mps3 = " \
    file://0001-PSA-revert-header-versions.patch;patchdir=external/tfm-psa-adac \
    file://0002-Fix-psa_key_handle_t-initialization.patch;patchdir=external/tfm-psa-adac \
    file://0003-cmake-Update-psa_adac_psa_crypto-dependencies.patch;patchdir=external/tfm-psa-adac \
    file://0001-Build-Fix-compiler-warnings.patch;patchdir=external/tfm-psa-adac \
    "

create_bl1_image(){
    dd conv=notrunc bs=1 if=${D}${FIRMWARE_DIR}/bl1_1.bin of=${D}${FIRMWARE_DIR}/bl1.bin seek=0
    # Size of bl1_1.bin is 58KB (59392 bytes)  
    dd conv=notrunc bs=1 if=${D}${FIRMWARE_DIR}/bl1_provisioning_bundle.bin of=${D}${FIRMWARE_DIR}/bl1.bin seek=59392
}
do_install[postfuncs] += "create_bl1_image"
