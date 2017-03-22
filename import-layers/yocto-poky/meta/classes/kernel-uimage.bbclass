inherit kernel-uboot

python __anonymous () {
    if "uImage" in (d.getVar('KERNEL_IMAGETYPES', True) or "").split():
        depends = d.getVar("DEPENDS", True)
        depends = "%s u-boot-mkimage-native" % depends
        d.setVar("DEPENDS", depends)

        # Override KERNEL_IMAGETYPE_FOR_MAKE variable, which is internal
        # to kernel.bbclass . We override the variable here, since we need
        # to build uImage using the kernel build system if and only if
        # KEEPUIMAGE == yes. Otherwise, we pack compressed vmlinux into
        # the uImage .
        if d.getVar("KEEPUIMAGE", True) != 'yes':
            typeformake = d.getVar("KERNEL_IMAGETYPE_FOR_MAKE", True) or ""
            if "uImage" in typeformake.split():
                d.setVar('KERNEL_IMAGETYPE_FOR_MAKE', typeformake.replace('uImage', 'vmlinux'))
}

do_uboot_mkimage() {
	if echo "${KERNEL_IMAGETYPES}" | grep -wq "uImage"; then
		if test "x${KEEPUIMAGE}" != "xyes" ; then
			uboot_prep_kimage

			ENTRYPOINT=${UBOOT_ENTRYPOINT}
			if test -n "${UBOOT_ENTRYSYMBOL}"; then
				ENTRYPOINT=`${HOST_PREFIX}nm ${S}/vmlinux | \
					awk '$3=="${UBOOT_ENTRYSYMBOL}" {print $1}'`
			fi

			uboot-mkimage -A ${UBOOT_ARCH} -O linux -T kernel -C "${linux_comp}" -a ${UBOOT_LOADADDRESS} -e $ENTRYPOINT -n "${DISTRO_NAME}/${PV}/${MACHINE}" -d linux.bin arch/${ARCH}/boot/uImage
			rm -f linux.bin
		fi
	fi
}

addtask uboot_mkimage before do_install after do_compile
