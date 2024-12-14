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
	if [ -e arch/${ARCH}/boot/compressed/vmlinux ]; then
		vmlinux_path="arch/${ARCH}/boot/compressed/vmlinux"
		linux_suffix=""
		linux_comp="none"
	elif [ -e arch/${ARCH}/boot/vmlinuz.bin ]; then
		rm -f linux.bin
		cp -l arch/${ARCH}/boot/vmlinuz.bin linux.bin
		vmlinux_path=""
		linux_suffix=""
		linux_comp="none"
	else
		vmlinux_path="vmlinux"
		# Use vmlinux.initramfs for linux.bin when INITRAMFS_IMAGE_BUNDLE set
		# As per the implementation in kernel.bbclass.
		# See do_bundle_initramfs function
		if [ "${INITRAMFS_IMAGE_BUNDLE}" = "1" ] && [ -e vmlinux.initramfs ]; then
			vmlinux_path="vmlinux.initramfs"
		fi
		linux_suffix="${FIT_KERNEL_COMP_ALG_EXTENSION}"
		linux_comp="${FIT_KERNEL_COMP_ALG}"
	fi

	[ -n "${vmlinux_path}" ] && ${KERNEL_OBJCOPY} -O binary -R .note -R .comment -S "${vmlinux_path}" linux.bin

	if [ "${linux_comp}" != "none" ] ; then
		if [ "${linux_comp}" = "gzip" ] ; then
			gzip -9 linux.bin
		elif [ "${linux_comp}" = "lzo" ] ; then
			lzop -9 linux.bin
		elif [ "${linux_comp}" = "lzma" ] ; then
			xz --format=lzma -f -6 linux.bin
		fi
		mv -f "linux.bin${linux_suffix}" linux.bin
	fi

	echo "${linux_comp}"
}
