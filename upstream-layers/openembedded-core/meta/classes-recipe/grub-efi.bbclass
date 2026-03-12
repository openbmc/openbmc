#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit grub-efi-cfg
require conf/image-uefi.conf

# Assure the existence of grub-efi image in deploy dir
do_bootimg[depends] += "grub-efi:do_deploy"

efi_populate() {
	efi_populate_common "$1" grub-efi

	install -m 0644 ${GRUB_CFG} ${DEST}${EFIDIR}/grub.cfg
}
