FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

#
# Corstone-500 MACHINE
#
SRC_URI:append:corstone500 = " \
                   file://0001-armv7-adding-generic-timer-access-through-MMIO.patch \
                   file://0002-board-arm-add-corstone500-board.patch"

#
# Corstone1000 64-bit machines
#
DEPENDS:append:corstone1000 = " gnutls-native"
CORSTONE1000_DEVICE_TREE:corstone1000-mps3 = "corstone1000-mps3"
CORSTONE1000_DEVICE_TREE:corstone1000-fvp = "corstone1000-fvp"
EXTRA_OEMAKE:append:corstone1000 = ' DEVICE_TREE=${CORSTONE1000_DEVICE_TREE}'

SYSROOT_DIRS:append:corstone1000 = " /boot"

SRC_URI:append:corstone1000 = " \
        file://0001-arm64-smccc-add-support-for-SMCCCv1.2-x0-x17-registe.patch          \ 
        file://0002-lib-uuid-introduce-uuid_str_to_le_bin-function.patch		\ 
        file://0003-arm_ffa-introduce-Arm-FF-A-low-level-driver.patch			\ 
        file://0004-arm_ffa-efi-unmap-RX-TX-buffers.patch				\ 
        file://0005-arm_ffa-introduce-armffa-command.patch				\ 
        file://0006-arm_ffa-introduce-the-FF-A-Sandbox-driver.patch			\ 
        file://0007-arm_ffa-introduce-Sandbox-test-cases-for-UCLASS_FFA.patch		\ 
        file://0008-arm_ffa-introduce-armffa-command-Sandbox-test.patch			\ 
        file://0009-arm_ffa-efi-introduce-FF-A-MM-communication.patch			\ 
        file://0010-arm_ffa-efi-corstone1000-enable-MM-communication.patch		\ 
        file://0011-efi-corstone1000-introduce-EFI-capsule-update.patch			\ 
        file://0012-arm-corstone1000-fix-unrecognized-filesystem-type.patch		\ 
        file://0013-efi_capsule-corstone1000-pass-interface-id-and-buffe.patch		\ 
        file://0014-efi_boottime-corstone1000-pass-interface-id-and-kern.patch		\ 
        file://0015-efi_loader-corstone1000-remove-guid-check-from-corst.patch		\ 
        file://0016-efi_loader-populate-ESRT-table-if-EFI_ESRT-config-op.patch		\ 
        file://0017-efi_firmware-add-get_image_info-for-corstone1000.patch		\ 
        file://0018-efi_loader-send-bootcomplete-message-to-secure-encla.patch		\ 
        file://0019-efi_loader-fix-null-pointer-exception-with-get_image.patch		\ 
        file://0020-arm-corstone1000-add-mmc-for-fvp.patch				\ 
        file://0021-corstone1000-add-compressed-kernel-support.patch			\ 
        file://0022-Introduce-external-sys-driver-to-device-tree.patch			\ 
        file://0023-Add-mhu-and-rpmsg-client-to-u-boot-device-tree.patch		\ 
        file://0024-arm-corstone1000-esrt-support.patch					\ 
        file://0025-efi_setup-discover-FF-A-bus-before-raising-EFI-start.patch		\ 
        file://0026-corstone1000-enable-distro-booting-command.patch                        \
        "

#
# FVP BASE
#
SRC_URI:append:fvp-base = " file://bootargs.cfg"

#
# FVP BASE ARM32
#
SRC_URI:append:fvp-base-arm32 = " file://0001-Add-vexpress_aemv8a_aarch32-variant.patch \
				  file://0002-Revert-vexpress64-Enable-OF_CONTROL-and-OF_BOARD-for.patch \
				"

#
# FVP BASER
#
SRC_URI:append:fvp-baser-aemv8r64 = " \
    file://0001-armv8-Add-ARMv8-MPU-configuration-logic.patch \
    file://0002-vexpress64-add-MPU-memory-map-for-the-BASER_FVP.patch \
    file://0003-armv8-Allow-disabling-exception-vectors-on-non-SPL-b.patch \
    file://0004-armv8-ARMV8_SWITCH_TO_EL1-improvements.patch \
    file://0005-armv8-Make-disabling-HVC-configurable-when-switching.patch \
    file://0006-vexpress64-Do-not-set-COUNTER_FREQUENCY.patch \
    file://0007-vexpress64-Enable-LIBFDT_OVERLAY-in-the-vexpress_aem.patch \
    file://0008-armv8-Allow-PRBAR-MPU-attributes-to-be-configured.patch \
    file://0009-armv8-Enable-icache-when-switching-exception-levels-.patch \
    "


#
# Juno Machines
#
SRC_URI:append:juno = " file://0001-configs-vexpress-modify-to-boot-compressed-initramfs.patch"


#
# TC0 and TC1 MACHINES
#
SRC_URI:append:tc = " \
        file://bootargs.cfg \
        file://0001-arm-total_compute-update-secure-dram-size.patch \
        file://0002-arm_ffa-introducing-Arm-FF-A-low-level-driver.patch \
        file://0003-arm-total_compute-enable-psci.patch \
        file://0004-arm_ffa-rxtx_map-should-use-64-bit-calls.patch \
        file://0005-efi_firmware-add-new-fmp-driver-that-supports-arm-fw.patch \
        file://0006-arm-total_compute-enable-capsule-update.patch \
        file://0007-arm_ffa-unmap-rxtx-buffer-before-exiting-u-boot.patch \
        "
