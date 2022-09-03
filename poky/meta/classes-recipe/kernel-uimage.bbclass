#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit kernel-uboot

python __anonymous () {
    if "uImage" in d.getVar('KERNEL_IMAGETYPES'):
        depends = d.getVar("DEPENDS")
        depends = "%s u-boot-tools-native" % depends
        d.setVar("DEPENDS", depends)

        # Override KERNEL_IMAGETYPE_FOR_MAKE variable, which is internal
        # to kernel.bbclass . We override the variable here, since we need
        # to build uImage using the kernel build system if and only if
        # KEEPUIMAGE == yes. Otherwise, we pack compressed vmlinux into
        # the uImage .
        if d.getVar("KEEPUIMAGE") != 'yes':
            typeformake = d.getVar("KERNEL_IMAGETYPE_FOR_MAKE") or ""
            if "uImage" in typeformake.split():
                d.setVar('KERNEL_IMAGETYPE_FOR_MAKE', typeformake.replace('uImage', 'vmlinux'))

            # Enable building of uImage with mkimage
            bb.build.addtask('do_uboot_mkimage', 'do_install', 'do_kernel_link_images', d)
}

do_uboot_mkimage[dirs] += "${B}"
do_uboot_mkimage() {
	uboot_prep_kimage

	ENTRYPOINT=${UBOOT_ENTRYPOINT}
	if [ -n "${UBOOT_ENTRYSYMBOL}" ]; then
		ENTRYPOINT=`${HOST_PREFIX}nm ${B}/vmlinux | \
			awk '$3=="${UBOOT_ENTRYSYMBOL}" {print "0x"$1;exit}'`
	fi

	uboot-mkimage -A ${UBOOT_ARCH} -O linux -T ${UBOOT_MKIMAGE_KERNEL_TYPE} -C "${linux_comp}" -a ${UBOOT_LOADADDRESS} -e $ENTRYPOINT -n "${DISTRO_NAME}/${PV}/${MACHINE}" -d linux.bin ${B}/arch/${ARCH}/boot/uImage
	rm -f linux.bin
}
