#
# While installing a rpm to update kernel on a deployed target, it will update
# the boot area and the boot menu with the kernel as the priority but allow
# you to fall back to the original kernel as well.
#
# - In kernel-image's preinstall scriptlet, it backs up original kernel to avoid
#   probable confliction with the new one.
#
# - In kernel-image's postinstall scriptlet, it modifies grub's config file to
#   updates the new kernel as the boot priority.
#

python __anonymous () {
    import re

    preinst = '''
	# Parsing confliction
	[ -f "$D/boot/grub/menu.list" ] && grubcfg="$D/boot/grub/menu.list"
	[ -f "$D/boot/grub/grub.cfg" ] && grubcfg="$D/boot/grub/grub.cfg"
	if [ -n "$grubcfg" ]; then
		# Dereference symlink to avoid confliction with new kernel name.
		if grep -q "/KERNEL_IMAGETYPE \+root=" $grubcfg; then
			if [ -L "$D/boot/KERNEL_IMAGETYPE" ]; then
				kimage=`realpath $D/boot/KERNEL_IMAGETYPE 2>/dev/null`
				if [ -f "$D$kimage" ]; then
					sed -i "s:KERNEL_IMAGETYPE \+root=:${kimage##*/} root=:" $grubcfg
				fi
			fi
		fi

		# Rename old kernel if it conflicts with new kernel name.
		if grep -q "/KERNEL_IMAGETYPE-${KERNEL_VERSION} \+root=" $grubcfg; then
			if [ -f "$D/boot/KERNEL_IMAGETYPE-${KERNEL_VERSION}" ]; then
				timestamp=`date +%s`
				kimage="$D/boot/KERNEL_IMAGETYPE-${KERNEL_VERSION}-$timestamp-back"
				sed -i "s:KERNEL_IMAGETYPE-${KERNEL_VERSION} \+root=:${kimage##*/} root=:" $grubcfg
				mv "$D/boot/KERNEL_IMAGETYPE-${KERNEL_VERSION}" "$kimage"
			fi
		fi
	fi
'''

    postinst = '''
	get_new_grub_cfg() {
		grubcfg="$1"
		old_image="$2"
		title="Update KERNEL_IMAGETYPE-${KERNEL_VERSION}-${PV}"
		if [ "${grubcfg##*/}" = "grub.cfg" ]; then
			rootfs=`grep " *linux \+[^ ]\+ \+root=" $grubcfg -m 1 | \
				 sed "s#${old_image}#${old_image%/*}/KERNEL_IMAGETYPE-${KERNEL_VERSION}#"`

			echo "menuentry \"$title\" {"
			echo "    set root=(hd0,1)"
			echo "$rootfs"
			echo "}"
		elif [ "${grubcfg##*/}" = "menu.list" ]; then
			rootfs=`grep "kernel \+[^ ]\+ \+root=" $grubcfg -m 1 | \
				 sed "s#${old_image}#${old_image%/*}/KERNEL_IMAGETYPE-${KERNEL_VERSION}#"`

			echo "default 0"
			echo "timeout 30"
			echo "title $title"
			echo "root  (hd0,0)"
			echo "$rootfs"
		fi
	}

	get_old_grub_cfg() {
		grubcfg="$1"
		if [ "${grubcfg##*/}" = "grub.cfg" ]; then
			cat "$grubcfg"
		elif [ "${grubcfg##*/}" = "menu.list" ]; then
			sed -e '/^default/d' -e '/^timeout/d' "$grubcfg"
		fi
	}

	if [ -f "$D/boot/grub/grub.cfg" ]; then
		grubcfg="$D/boot/grub/grub.cfg"
		old_image=`grep ' *linux \+[^ ]\+ \+root=' -m 1 "$grubcfg" | awk '{print $2}'`
	elif [ -f "$D/boot/grub/menu.list" ]; then
		grubcfg="$D/boot/grub/menu.list"
		old_image=`grep '^kernel \+[^ ]\+ \+root=' -m 1 "$grubcfg" | awk '{print $2}'`
	fi

	# Don't update grubcfg at first install while old bzImage doesn't exist.
	if [ -f "$D/boot/${old_image##*/}" ]; then
		grubcfgtmp="$grubcfg.tmp"
		get_new_grub_cfg "$grubcfg" "$old_image"  > $grubcfgtmp
		get_old_grub_cfg "$grubcfg" >> $grubcfgtmp
		mv $grubcfgtmp $grubcfg
		echo "Caution! Update kernel may affect kernel-module!"
	fi
'''

    imagetypes = d.getVar('KERNEL_IMAGETYPES')
    imagetypes = re.sub(r'\.gz$', '', imagetypes)

    for type in imagetypes.split():
        typelower = type.lower()
        preinst_append = preinst.replace('KERNEL_IMAGETYPE', type)
        postinst_prepend = postinst.replace('KERNEL_IMAGETYPE', type)
        d.setVar('pkg_preinst_kernel-image-' + typelower + '_append', preinst_append)
        d.setVar('pkg_postinst_kernel-image-' + typelower + '_prepend', postinst_prepend)
}

