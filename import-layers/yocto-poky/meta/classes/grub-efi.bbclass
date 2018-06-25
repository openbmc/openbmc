inherit grub-efi-cfg

efi_populate() {
	# DEST must be the root of the image so that EFIDIR is not
	# nested under a top level directory.
	DEST=$1

	install -d ${DEST}${EFIDIR}

	GRUB_IMAGE="grub-efi-bootia32.efi"
	DEST_IMAGE="bootia32.efi"
	if [ "${TARGET_ARCH}" = "x86_64" ]; then
		GRUB_IMAGE="grub-efi-bootx64.efi"
		DEST_IMAGE="bootx64.efi"
	fi
	install -m 0644 ${DEPLOY_DIR_IMAGE}/${GRUB_IMAGE} ${DEST}${EFIDIR}/${DEST_IMAGE}
	EFIPATH=$(echo "${EFIDIR}" | sed 's/\//\\/g')
	printf 'fs0:%s\%s\n' "$EFIPATH" "$DEST_IMAGE" >${DEST}/startup.nsh

	install -m 0644 ${GRUB_CFG} ${DEST}${EFIDIR}/grub.cfg
}

efi_iso_populate() {
	iso_dir=$1
	efi_populate $iso_dir
	# Build a EFI directory to create efi.img
	mkdir -p ${EFIIMGDIR}/${EFIDIR}
	cp $iso_dir/${EFIDIR}/* ${EFIIMGDIR}${EFIDIR}
	cp $iso_dir/${KERNEL_IMAGETYPE} ${EFIIMGDIR}
	EFIPATH=$(echo "${EFIDIR}" | sed 's/\//\\/g')
	printf 'fs0:%s\%s\n' "$EFIPATH" "$GRUB_IMAGE" > ${EFIIMGDIR}/startup.nsh
	if [ -f "$iso_dir/initrd" ] ; then
		cp $iso_dir/initrd ${EFIIMGDIR}
	fi
}

efi_hddimg_populate() {
	efi_populate $1
}
