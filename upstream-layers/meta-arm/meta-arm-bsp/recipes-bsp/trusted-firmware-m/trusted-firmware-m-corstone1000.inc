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
    file://0015-CC312-Add-barrier-before-first-AO-lock-write.patch \
    file://0016-Platform-CS1K-make-mutlicore-support-platform-generi.patch \
    file://0017-lib-efi_guid-Added-EFI-GUID-library.patch \
    file://0018-lib-efi_soft_crc-Added-EFI-CRC-library.patch \
    file://0019-lib-gpt-Implemented-generic-GPT-parser-for-flash.patch \
    file://0020-lib-gpt-Expanded-how-GPT-partition-can-be-identified.patch \
    file://0021-lib-gpt-Added-operations-to-modify-partitions.patch \
    file://0022-lib-gpt-Added-operation-to-move-entry.patch \
    file://0023-lib-gpt-Added-ability-to-create-and-remove-partition.patch \
    file://0024-lib-gpt-Added-table-validation-operations.patch \
    file://0025-lib-gpt-Added-defragmentation-operation.patch \
    file://0026-lib-GPT-Fix-cppcheck-warnings.patch \
    file://0027-lib-efi_guid-Remove-unecessary-include-folder.patch \
    file://0028-lib-efi_guid-Correct-included-folder.patch \
    file://0029-lib-efi_soft_crc-Correct-include-directory.patch \
    file://0030-lib-gpt-Add-missing-link-library.patch \
    file://0031-lib-gpt-Correct-variable-name-used.patch \
    file://0032-lib-gpt-Correct-include-directory.patch \
    file://0033-lib-gpt-Move-contents-of-CMake-config-file.patch \
    file://0034-plat-cs1k-Fixed-formatting-errors.patch \
    file://0035-plat-cs1k-Removed-unused-variables.patch \
    file://0036-plat-cs1k-Fixed-bad-function-returns.patch \
    file://0037-plat-cs1k-Improved-logging-in-function.patch \
    file://0038-plat-cs1k-Remove-unused-function.patch \
    file://0039-plat-cs1k-Reduce-BL1-binary-size.patch \
    file://0040-plat-cs1k-Update-license-identifier.patch \
    file://0041-plat-cs1k-Changed-to-use-new-GPT-library.patch \
    file://0042-plat-cs1k-Move-variable-from-stack-to-data.patch \
    file://0043-plat-cs1k-Create-and-remove-FWU-image-partitions.patch \
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
    # Size of bl1_1.bin is 58KB (59208 bytes)
    dd conv=notrunc bs=1 if=${D}${FIRMWARE_DIR}/bl1_provisioning_bundle.bin of=${D}${FIRMWARE_DIR}/bl1.bin seek=59208
}
do_install[postfuncs] += "create_bl1_image"
