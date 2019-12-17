inherit grub-efi-cfg
require conf/image-uefi.conf

efi_populate() {
	efi_populate_common "$1" grub-efi

	install -m 0644 ${GRUB_CFG} ${DEST}${EFIDIR}/grub.cfg
}
