# fitImage kernel compression algorithm
FIT_KERNEL_COMP_ALG ?= "gzip"
FIT_KERNEL_COMP_ALG_EXTENSION ?= ".gz"

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
		linux_suffix="${FIT_KERNEL_COMP_ALG_EXTENSION}"
		linux_comp="${FIT_KERNEL_COMP_ALG}"
	fi

	[ -n "${vmlinux_path}" ] && ${OBJCOPY} -O binary -R .note -R .comment -S "${vmlinux_path}" linux.bin

	if [ "${linux_comp}" != "none" ] ; then
		gzip -9 linux.bin
		mv -f "linux.bin${linux_suffix}" linux.bin
	fi

	echo "${linux_comp}"
}
