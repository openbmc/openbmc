# Essentially kernel-uimage, but for cuImage.

inherit kernel-uboot

KBUILD_HAS_CUIMAGE ?= "no"

python __anonymous () {
    if "cuImage" in (d.getVar('KERNEL_IMAGETYPES', True) or "").split():
        depends = d.getVar("DEPENDS", True)
        depends = "%s u-boot-mkimage-native" % depends
        d.setVar("DEPENDS", depends)

	# Override KERNEL_IMAGETYPE_FOR_MAKE variable, which is internal
	# to kernel.bbclass . We override the variable here, since we need
	# to build cuImage using the kernel build system if and only if
	# KBUILD_HAS_CUIMAGE == yes. Otherwise, we pack compressed vmlinux into
	# the cuImage .
	if d.getVar("KBUILD_HAS_CUIMAGE", True) != 'yes':
	    typeformake = d.getVar("KERNEL_IMAGETYPE_FOR_MAKE", True) or ""
	    if "cuImage" in typeformake.split():
	        typeformake = typeformake.replace('cuImage', 'zImage')
            d.setVar("KERNEL_IMAGETYPE_FOR_MAKE", typeformake)
}

do_uboot_mkcimage() {
        dt="arch/${ARCH}/boot/dts/${KERNEL_DEVICETREE}"
        if ! test -r $dt; then
		dt=""
        fi

	if echo "${KERNEL_IMAGETYPES}" | grep -wq "cuImage" ; then
		if test "x${KBUILD_HAS_CUIMAGE}" != "xyes" ; then
			uboot_prep_kimage
                        cat linux.bin $dt > linux-dts.bin
			ENTRYPOINT=${UBOOT_ENTRYPOINT}
			if test -n "${UBOOT_ENTRYSYMBOL}"; then
				ENTRYPOINT=`${HOST_PREFIX}nm ${S}/vmlinux | \
					awk '$3=="${UBOOT_ENTRYSYMBOL}" {print $1}'`
			fi

			uboot-mkimage -A ${UBOOT_ARCH} -O linux -T kernel -C "${linux_comp}" -a ${UBOOT_LOADADDRESS} -e $ENTRYPOINT -n "${DISTRO_NAME}/${PV}/${MACHINE}" -d linux-dts.bin arch/${ARCH}/boot/cuImage
			rm -f linux.bin linux-dts.bin
		fi
	fi
}

addtask uboot_mkcimage before do_install after do_compile
