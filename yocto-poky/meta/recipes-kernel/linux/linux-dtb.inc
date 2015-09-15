# Support for device tree generation
FILES_kernel-devicetree = "/${KERNEL_IMAGEDEST}/devicetree*"

python __anonymous () {
    d.appendVar("PACKAGES", " kernel-devicetree")
}

do_compile_append() {
	if test -n "${KERNEL_DEVICETREE}"; then
		for DTB in ${KERNEL_DEVICETREE}; do
			if echo ${DTB} | grep -q '/dts/'; then
				bbwarn "${DTB} contains the full path to the the dts file, but only the dtb name should be used."
				DTB=`basename ${DTB} | sed 's,\.dts$,.dtb,g'`
			fi
			oe_runmake ${DTB}
		done
	fi
}

do_install_append() {
	if test -n "${KERNEL_DEVICETREE}"; then
		for DTB in ${KERNEL_DEVICETREE}; do
			if echo ${DTB} | grep -q '/dts/'; then
				bbwarn "${DTB} contains the full path to the the dts file, but only the dtb name should be used."
				DTB=`basename ${DTB} | sed 's,\.dts$,.dtb,g'`
			fi
			DTB_BASE_NAME=`basename ${DTB} .dtb`
			DTB_SYMLINK_NAME=`echo ${KERNEL_IMAGE_SYMLINK_NAME} | sed "s/${MACHINE}/${DTB_BASE_NAME}/g"`
			DTB_PATH="${B}/arch/${ARCH}/boot/dts/${DTB}"
			if [ ! -e "${DTB_PATH}" ]; then
				DTB_PATH="${B}/arch/${ARCH}/boot/${DTB}"
			fi
			install -m 0644 ${DTB_PATH} ${D}/${KERNEL_IMAGEDEST}/devicetree-${DTB_SYMLINK_NAME}.dtb
		done
	fi
}

do_deploy_append() {
	if test -n "${KERNEL_DEVICETREE}"; then
		for DTB in ${KERNEL_DEVICETREE}; do
			if echo ${DTB} | grep -q '/dts/'; then
				bbwarn "${DTB} contains the full path to the the dts file, but only the dtb name should be used."
				DTB=`basename ${DTB} | sed 's,\.dts$,.dtb,g'`
			fi
			DTB_BASE_NAME=`basename ${DTB} .dtb`
			DTB_NAME=`echo ${KERNEL_IMAGE_BASE_NAME} | sed "s/${MACHINE}/${DTB_BASE_NAME}/g"`
			DTB_SYMLINK_NAME=`echo ${KERNEL_IMAGE_SYMLINK_NAME} | sed "s/${MACHINE}/${DTB_BASE_NAME}/g"`
			DTB_PATH="${B}/arch/${ARCH}/boot/dts/${DTB}"
			if [ ! -e "${DTB_PATH}" ]; then
				DTB_PATH="${B}/arch/${ARCH}/boot/${DTB}"
			fi
			install -d ${DEPLOYDIR}
			install -m 0644 ${DTB_PATH} ${DEPLOYDIR}/${DTB_NAME}.dtb
			cd ${DEPLOYDIR}
			ln -sf ${DTB_NAME}.dtb ${DTB_SYMLINK_NAME}.dtb
			cd -
		done
	fi
}

pkg_postinst_kernel-devicetree () {
	cd /${KERNEL_IMAGEDEST}
	for DTB_FILE in ${KERNEL_DEVICETREE}
	do
		DTB_BASE_NAME=`basename ${DTB_FILE} | awk -F "." '{print $1}'`
		DTB_SYMLINK_NAME=`echo ${KERNEL_IMAGE_SYMLINK_NAME} | sed "s/${MACHINE}/${DTB_BASE_NAME}/g"`
		update-alternatives --install /${KERNEL_IMAGEDEST}/${DTB_BASE_NAME}.dtb ${DTB_BASE_NAME}.dtb devicetree-${DTB_SYMLINK_NAME}.dtb ${KERNEL_PRIORITY} || true
	done
}

pkg_postrm_kernel-devicetree () {
	cd /${KERNEL_IMAGEDEST}
	for DTB_FILE in ${KERNEL_DEVICETREE}
	do
		DTB_BASE_NAME=`basename ${DTB_FILE} | awk -F "." '{print $1}'`
		DTB_SYMLINK_NAME=`echo ${KERNEL_IMAGE_SYMLINK_NAME} | sed "s/${MACHINE}/${DTB_BASE_NAME}/g"`
		update-alternatives --remove ${DTB_BASE_NAME}.dtb devicetree-${DTB_SYMLINK_NAME}.dtb ${KERNEL_PRIORITY} || true
	done
}
