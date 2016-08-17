SYSLINUX_TIMEOUT = "10"
SYSLINUX_LABEL = "boot"
SYSLINUX_XEN_APPEND = "dom0_mem=1048576"
SYSLINUX_KERNEL_APPEND = "ramdisk_size=32768 root=/dev/ram0 rw console=tty0 console=ttyS0,115200n8"
#LABELS_append = " ${SYSLINUX_LABEL} "

INITRD = "${DEPLOY_DIR_IMAGE}/${IMAGE_BASENAME}-${MACHINE}.cpio.lzma"

do_bootimg[depends] += "${IMAGE_BASENAME}:do_rootfs"

inherit bootimg

syslinux_populate_append() {
	install -m 0444 ${STAGING_LIBDIR}/syslinux/mboot.c32 ${HDDDIR}${SYSLINUXDIR}/mboot.c32
}

grubefi_populate_append() {
	install -m 0644 ${DEPLOY_DIR_IMAGE}/xen-${MACHINE}.gz ${DEST}${EFIDIR}/xen.gz
}

populate_append() {
	install -m 0644 ${DEPLOY_DIR_IMAGE}/xen-${MACHINE}.gz ${DEST}/xen.gz
}

build_syslinux_cfg() {
	echo ALLOWOPTIONS 1 > ${SYSLINUXCFG}
	echo SERIAL 0 115200 > ${SYSLINUXCFG}
	echo DEFAULT ${SYSLINUX_LABEL} >> ${SYSLINUXCFG}
	echo TIMEOUT ${SYSLINUX_TIMEOUT} >> ${SYSLINUXCFG}
	echo PROMPT 1 >> ${SYSLINUXCFG}
	echo LABEL ${SYSLINUX_LABEL} >> ${SYSLINUXCFG}
	echo KERNEL mboot.c32 >> ${SYSLINUXCFG}
	echo APPEND xen.gz ${SYSLINUX_XEN_APPEND} --- vmlinuz ${SYSLINUX_KERNEL_APPEND} --- initrd >> ${SYSLINUXCFG}
}

