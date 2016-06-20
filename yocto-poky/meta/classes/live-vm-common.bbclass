# Some of the vars for vm and live image are conflicted, this function
# is used for fixing the problem.
def set_live_vm_vars(d, suffix):
    vars = ['GRUB_CFG', 'SYSLINUX_CFG', 'ROOT', 'LABELS', 'INITRD']
    for var in vars:
        var_with_suffix = var + '_' + suffix
        if d.getVar(var, True):
            bb.warn('Found potential conflicted var %s, please use %s rather than %s' % \
                (var, var_with_suffix, var))
        elif d.getVar(var_with_suffix, True):
            d.setVar(var, d.getVar(var_with_suffix, True))


EFI = "${@bb.utils.contains("MACHINE_FEATURES", "efi", "1", "0", d)}"
EFI_PROVIDER ?= "grub-efi"
EFI_CLASS = "${@bb.utils.contains("MACHINE_FEATURES", "efi", "${EFI_PROVIDER}", "", d)}"

# Include legacy boot if MACHINE_FEATURES includes "pcbios" or if it does not
# contain "efi". This way legacy is supported by default if neither is
# specified, maintaining the original behavior.
def pcbios(d):
    pcbios = bb.utils.contains("MACHINE_FEATURES", "pcbios", "1", "0", d)
    if pcbios == "0":
        pcbios = bb.utils.contains("MACHINE_FEATURES", "efi", "0", "1", d)
    return pcbios

PCBIOS = "${@pcbios(d)}"
PCBIOS_CLASS = "${@['','syslinux'][d.getVar('PCBIOS', True) == '1']}"

inherit ${EFI_CLASS}
inherit ${PCBIOS_CLASS}

KERNEL_IMAGETYPE ??= "bzImage"

populate_kernel() {
	dest=$1
	install -d $dest

	# Install bzImage, initrd, and rootfs.img in DEST for all loaders to use.
	if [ -e ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE} ]; then
		install -m 0644 ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE} $dest/vmlinuz
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

