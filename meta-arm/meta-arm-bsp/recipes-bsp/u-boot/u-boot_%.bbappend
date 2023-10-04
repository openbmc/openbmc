FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

#
# Corstone1000 64-bit machines
#
DEPENDS:append:corstone1000 = " gnutls-native openssl-native efitools-native"
CORSTONE1000_DEVICE_TREE:corstone1000-mps3 = "corstone1000-mps3"
CORSTONE1000_DEVICE_TREE:corstone1000-fvp = "corstone1000-fvp"
EXTRA_OEMAKE:append:corstone1000 = ' DEVICE_TREE=${CORSTONE1000_DEVICE_TREE}'

SYSROOT_DIRS:append:corstone1000 = " /boot"

SRC_URI:append:corstone1000 = " \
        file://0001-FF-A-v15-arm64-smccc-add-support-for-SMCCCv1.2-x0-x1.patch    \
        file://0002-FF-A-v15-lib-uuid-introduce-uuid_str_to_le_bin-funct.patch	  \
        file://0003-FF-A-v15-lib-uuid-introduce-testcase-for-uuid_str_to.patch	  \
	file://0004-FF-A-v15-arm_ffa-introduce-Arm-FF-A-support.patch		  \
	file://0005-FF-A-v15-arm_ffa-introduce-armffa-command.patch		  \
	file://0006-FF-A-v15-arm_ffa-introduce-sandbox-FF-A-support.patch	  \
        file://0007-FF-A-v15-arm_ffa-introduce-sandbox-test-cases-for-UC.patch	  \
        file://0008-FF-A-v15-arm_ffa-introduce-armffa-command-Sandbox-te.patch	  \
	file://0009-FF-A-v15-arm_ffa-efi-introduce-FF-A-MM-communication.patch	  \
	file://0010-FF-A-v15-arm_ffa-efi-corstone1000-enable-MM-communic.patch	  \
	file://0011-efi-corstone1000-fwu-introduce-EFI-capsule-update.patch	  \
	file://0012-arm-corstone1000-fix-unrecognized-filesystem-type.patch	  \
	file://0013-efi_loader-corstone1000-remove-guid-check-from-corst.patch	  \
	file://0014-efi_loader-populate-ESRT-table-if-EFI_ESRT-config-op.patch	  \
	file://0015-efi_firmware-add-get_image_info-for-corstone1000.patch	  \
	file://0016-efi_loader-fix-null-pointer-exception-with-get_image.patch	  \
	file://0017-arm-corstone1000-add-mmc-for-fvp.patch			  \
	file://0018-corstone1000-add-compressed-kernel-support.patch		  \
	file://0019-Introduce-external-sys-driver-to-device-tree.patch		  \
	file://0020-Add-mhu-and-rpmsg-client-to-u-boot-device-tree.patch	  \
	file://0021-arm-corstone1000-esrt-support.patch			  \
	file://0022-corstone1000-enable-distro-booting-command.patch		  \
	file://0023-corstone1000-add-fwu-metadata-store-info.patch		  \
	file://0024-fwu_metadata-make-sure-structures-are-packed.patch		  \
	file://0025-corstone1000-add-boot-index.patch				  \
	file://0026-corstone1000-adjust-boot-bank-and-kernel-location.patch	  \
	file://0027-corstone1000-add-nvmxip-fwu-mdata-and-gpt-options.patch	  \
	file://0028-nvmxip-move-header-to-include.patch			  \
	file://0029-corstone1000-set-kernel_addr-based-on-boot_idx.patch	  \
	file://0030-corstone1000-boot-index-from-active.patch			  \
	file://0031-corstone1000-enable-PSCI-reset.patch			  \
	file://0032-Enable-EFI-set-get-time-services.patch			  \
	file://0033-corstone1000-detect-inflated-kernel-size.patch			  \
	file://0034-corstone1000-ESRT-add-unique-firmware-GUID.patch		\
	file://0035-dt-Provide-a-way-to-remove-non-compliant-nodes-and-p.patch \
	file://0036-bootefi-Call-the-EVT_FT_FIXUP-event-handler.patch \
	file://0037-corstone1000-purge-U-Boot-specific-DT-nodes.patch \
	file://0038-corstone1000-add-signature-device-tree-overlay.patch	  \
	file://0039-corstone1000-enable-authenticated-capsule-config.patch	  \
	file://0040-corstone1000-introduce-EFI-authenticated-capsule-upd.patch	  \
        "

do_configure:append:corstone1000(){
    openssl req -x509 -sha256 -newkey rsa:2048 -subj /CN=CRT/ -keyout ${B}/CRT.key -out ${B}/CRT.crt -nodes -days 365
    cert-to-efi-sig-list ${B}/CRT.crt ${B}/corstone1000_defconfig/CRT.esl
}

do_install:append:corstone1000() {
   install -D -p -m 0644 ${B}/CRT.crt ${DEPLOY_DIR_IMAGE}/corstone1000_capsule_cert.crt
   install -D -p -m 0644 ${B}/CRT.key ${DEPLOY_DIR_IMAGE}/corstone1000_capsule_key.key
}

#
# FVP BASE
#
SRC_URI:append:fvp-base = " file://bootargs.cfg \
	file://0001-Revert-vexpress64-pick-DRAM-size-from-DT.patch \
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
        "
