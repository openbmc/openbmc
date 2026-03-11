#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# fitImage kernel compression algorithm
FIT_KERNEL_COMP_ALG ?= "gzip"
FIT_KERNEL_COMP_ALG_EXTENSION ?= ".gz"

# Kernel image type passed to mkimage (i.e. kernel kernel_noload...)
UBOOT_MKIMAGE_KERNEL_TYPE ?= "kernel"

uboot_prep_kimage() {
	output_dir=$1
	# For backward compatibility with kernel-fitimage.bbclass and kernel-uboot.bbclass
	# support calling without parameter as well
	if [ -z "$output_dir" ]; then
		output_dir='.'
	fi

	linux_bin=$output_dir/linux.bin
	if [ -e "arch/${ARCH}/boot/compressed/vmlinux" ]; then
		vmlinux_path="arch/${ARCH}/boot/compressed/vmlinux"
		linux_suffix=""
		linux_comp="none"
	elif [ -e "arch/${ARCH}/boot/vmlinuz.bin" ]; then
		rm -f "$linux_bin"
		cp -l "arch/${ARCH}/boot/vmlinuz.bin" "$linux_bin"
		vmlinux_path=""
		linux_suffix=""
		linux_comp="none"
	else
		vmlinux_path="vmlinux"
		# Use vmlinux.initramfs for $linux_bin when INITRAMFS_IMAGE_BUNDLE set
		# As per the implementation in kernel.bbclass.
		# See do_bundle_initramfs function
		if [ "${INITRAMFS_IMAGE_BUNDLE}" = "1" ] && [ -e vmlinux.initramfs ]; then
			vmlinux_path="vmlinux.initramfs"
		fi
		linux_suffix="${FIT_KERNEL_COMP_ALG_EXTENSION}"
		linux_comp="${FIT_KERNEL_COMP_ALG}"
	fi

	[ -n "$vmlinux_path" ] && ${KERNEL_OBJCOPY} -O binary -R .note -R .comment -S "$vmlinux_path" "$linux_bin"

	if [ "$linux_comp" != "none" ] ; then
		if [ "$linux_comp" = "gzip" ] ; then
			gzip -9 "$linux_bin"
		elif [ "$linux_comp" = "lzo" ] ; then
			lzop -9 "$linux_bin"
		elif [ "$linux_comp" = "lzma" ] ; then
			xz --format=lzma -f -6 "$linux_bin"
		fi
		mv -f "$linux_bin$linux_suffix" "$linux_bin"
	fi

	printf "$linux_comp" > "$output_dir/linux_comp"
}
