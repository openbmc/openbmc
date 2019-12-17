# Some of the vars for vm and live image are conflicted, this function
# is used for fixing the problem.
def set_live_vm_vars(d, suffix):
    vars = ['GRUB_CFG', 'SYSLINUX_CFG', 'ROOT', 'LABELS', 'INITRD']
    for var in vars:
        var_with_suffix = var + '_' + suffix
        if d.getVar(var):
            bb.warn('Found potential conflicted var %s, please use %s rather than %s' % \
                (var, var_with_suffix, var))
        elif d.getVar(var_with_suffix):
            d.setVar(var, d.getVar(var_with_suffix))


EFI = "${@bb.utils.contains("MACHINE_FEATURES", "efi", "1", "0", d)}"
EFI_PROVIDER ?= "grub-efi"
EFI_CLASS = "${@bb.utils.contains("MACHINE_FEATURES", "efi", "${EFI_PROVIDER}", "", d)}"

MKDOSFS_EXTRAOPTS ??= "-S 512"

# Include legacy boot if MACHINE_FEATURES includes "pcbios" or if it does not
# contain "efi". This way legacy is supported by default if neither is
# specified, maintaining the original behavior.
def pcbios(d):
    pcbios = bb.utils.contains("MACHINE_FEATURES", "pcbios", "1", "0", d)
    if pcbios == "0":
        pcbios = bb.utils.contains("MACHINE_FEATURES", "efi", "0", "1", d)
    return pcbios

PCBIOS = "${@pcbios(d)}"
PCBIOS_CLASS = "${@['','syslinux'][d.getVar('PCBIOS') == '1']}"

# efi_populate_common DEST BOOTLOADER
efi_populate_common() {
        # DEST must be the root of the image so that EFIDIR is not
        # nested under a top level directory.
        DEST=$1

        install -d ${DEST}${EFIDIR}

        install -m 0644 ${DEPLOY_DIR_IMAGE}/$2-${EFI_BOOT_IMAGE} ${DEST}${EFIDIR}/${EFI_BOOT_IMAGE}
        EFIPATH=$(echo "${EFIDIR}" | sed 's/\//\\/g')
        printf 'fs0:%s\%s\n' "$EFIPATH" "${EFI_BOOT_IMAGE}" >${DEST}/startup.nsh
}

efi_iso_populate() {
        iso_dir=$1
        efi_populate $iso_dir
        # Build a EFI directory to create efi.img
        mkdir -p ${EFIIMGDIR}/${EFIDIR}
        cp $iso_dir/${EFIDIR}/* ${EFIIMGDIR}${EFIDIR}
        cp $iso_dir/${KERNEL_IMAGETYPE} ${EFIIMGDIR}

        EFIPATH=$(echo "${EFIDIR}" | sed 's/\//\\/g')
        printf 'fs0:%s\%s\n' "$EFIPATH" "${EFI_BOOT_IMAGE}" >${EFIIMGDIR}/startup.nsh

        if [ -f "$iso_dir/initrd" ] ; then
                cp $iso_dir/initrd ${EFIIMGDIR}
        fi
}

efi_hddimg_populate() {
	efi_populate $1
}

inherit ${EFI_CLASS}
inherit ${PCBIOS_CLASS}

populate_kernel() {
	dest=$1
	install -d $dest

	# Install bzImage, initrd, and rootfs.img in DEST for all loaders to use.
	bbnote "Trying to install ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE} as $dest/${KERNEL_IMAGETYPE}"
	if [ -e ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE} ]; then
		install -m 0644 ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE} $dest/${KERNEL_IMAGETYPE}
	else
		bbwarn "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE} doesn't exist"
	fi

	# initrd is made of concatenation of multiple filesystem images
	if [ -n "${INITRD}" ]; then
		rm -f $dest/initrd
		for fs in ${INITRD}
		do
			if [ -s "$fs" ]; then
				cat $fs >> $dest/initrd
			else
				bbfatal "$fs is invalid. initrd image creation failed."
			fi
		done
		chmod 0644 $dest/initrd
	fi
}

