# Support for device tree generation
PACKAGES_append = " \
    kernel-devicetree \
    ${@['kernel-image-zimage-bundle', ''][d.getVar('KERNEL_DEVICETREE_BUNDLE') != '1']} \
"
FILES_kernel-devicetree = "/${KERNEL_IMAGEDEST}/*.dtb /${KERNEL_IMAGEDEST}/*.dtbo"
FILES_kernel-image-zimage-bundle = "/${KERNEL_IMAGEDEST}/zImage-*.dtb.bin"

# Generate kernel+devicetree bundle
KERNEL_DEVICETREE_BUNDLE ?= "0"

normalize_dtb () {
	DTB="$1"
	if echo ${DTB} | grep -q '/dts/'; then
		bbwarn "${DTB} contains the full path to the the dts file, but only the dtb name should be used."
		DTB=`basename ${DTB} | sed 's,\.dts$,.dtb,g'`
	fi
	echo "${DTB}"
}

get_real_dtb_path_in_kernel () {
	DTB="$1"
	DTB_PATH="${B}/arch/${ARCH}/boot/dts/${DTB}"
	if [ ! -e "${DTB_PATH}" ]; then
		DTB_PATH="${B}/arch/${ARCH}/boot/${DTB}"
	fi
	echo "${DTB_PATH}"
}

do_configure_append() {
	if [ "${KERNEL_DEVICETREE_BUNDLE}" = "1" ]; then
		if echo ${KERNEL_IMAGETYPE_FOR_MAKE} | grep -q 'zImage'; then
			case "${ARCH}" in
				"arm")
				config="${B}/.config"
				if ! grep -q 'CONFIG_ARM_APPENDED_DTB=y' $config; then
					bbwarn 'CONFIG_ARM_APPENDED_DTB is NOT enabled in the kernel. Enabling it to allow the kernel to boot with the Device Tree appended!'
					sed -i "/CONFIG_ARM_APPENDED_DTB[ =]/d" $config
					echo "CONFIG_ARM_APPENDED_DTB=y" >> $config
					echo "# CONFIG_ARM_ATAG_DTB_COMPAT is not set" >> $config
				fi
				;;
				*)
				bberror "KERNEL_DEVICETREE_BUNDLE is not supported for ${ARCH}. Currently it is only supported for 'ARM'."
			esac
		else
			bberror 'The KERNEL_DEVICETREE_BUNDLE requires the KERNEL_IMAGETYPE to contain zImage.'
		fi
	fi
}

do_compile_append() {
	for DTB in ${KERNEL_DEVICETREE}; do
		DTB=`normalize_dtb "${DTB}"`
		oe_runmake ${DTB}
	done
}

do_install_append() {
	for DTB in ${KERNEL_DEVICETREE}; do
		DTB=`normalize_dtb "${DTB}"`
		DTB_EXT=${DTB##*.}
		DTB_PATH=`get_real_dtb_path_in_kernel "${DTB}"`
		DTB_BASE_NAME=`basename ${DTB} ."${DTB_EXT}"`
		install -m 0644 ${DTB_PATH} ${D}/${KERNEL_IMAGEDEST}/${DTB_BASE_NAME}.${DTB_EXT}
		for type in ${KERNEL_IMAGETYPE_FOR_MAKE}; do
			symlink_name=${type}"-"${KERNEL_IMAGE_SYMLINK_NAME}
			DTB_SYMLINK_NAME=`echo ${symlink_name} | sed "s/${MACHINE}/${DTB_BASE_NAME}/g"`
			ln -sf ${DTB_BASE_NAME}.${DTB_EXT} ${D}/${KERNEL_IMAGEDEST}/devicetree-${DTB_SYMLINK_NAME}.${DTB_EXT}

			if [ "$type" = "zImage" ] && [ "${KERNEL_DEVICETREE_BUNDLE}" = "1" ]; then
				cat ${D}/${KERNEL_IMAGEDEST}/$type \
					${D}/${KERNEL_IMAGEDEST}/${DTB_BASE_NAME}.${DTB_EXT} \
					> ${D}/${KERNEL_IMAGEDEST}/$type-${DTB_BASE_NAME}.${DTB_EXT}.bin
			fi
		done
	done
}

do_deploy_append() {
	for DTB in ${KERNEL_DEVICETREE}; do
		DTB=`normalize_dtb "${DTB}"`
		DTB_EXT=${DTB##*.}
		DTB_BASE_NAME=`basename ${DTB} ."${DTB_EXT}"`
		for type in ${KERNEL_IMAGETYPE_FOR_MAKE}; do
			base_name=${type}"-"${KERNEL_IMAGE_BASE_NAME}
			symlink_name=${type}"-"${KERNEL_IMAGE_SYMLINK_NAME}
			DTB_NAME=`echo ${base_name} | sed "s/${MACHINE}/${DTB_BASE_NAME}/g"`
			DTB_SYMLINK_NAME=`echo ${symlink_name} | sed "s/${MACHINE}/${DTB_BASE_NAME}/g"`
			DTB_PATH=`get_real_dtb_path_in_kernel "${DTB}"`
			install -d ${DEPLOYDIR}
			install -m 0644 ${DTB_PATH} ${DEPLOYDIR}/${DTB_NAME}.${DTB_EXT}
			ln -sf ${DTB_NAME}.${DTB_EXT} ${DEPLOYDIR}/${DTB_SYMLINK_NAME}.${DTB_EXT}
			ln -sf ${DTB_NAME}.${DTB_EXT} ${DEPLOYDIR}/${DTB_BASE_NAME}.${DTB_EXT}

			if [ "$type" = "zImage" ] && [ "${KERNEL_DEVICETREE_BUNDLE}" = "1" ]; then
				cat ${DEPLOYDIR}/$type \
					${DEPLOYDIR}/${DTB_NAME}.${DTB_EXT} \
					> ${DEPLOYDIR}/${DTB_NAME}.${DTB_EXT}.bin
				ln -sf ${DTB_NAME}.${DTB_EXT}.bin ${DEPLOYDIR}/$type-${DTB_BASE_NAME}.${DTB_EXT}.bin

				if [ -e "${KERNEL_OUTPUT_DIR}/${type}.initramfs" ]; then
					cat ${KERNEL_OUTPUT_DIR}/${type}.initramfs \
						${DEPLOYDIR}/${DTB_NAME}.${DTB_EXT} \
						> ${DEPLOYDIR}/${type}-${INITRAMFS_BASE_NAME}-${DTB_BASE_NAME}.${DTB_EXT}.bin
					ln -sf ${type}-${INITRAMFS_BASE_NAME}-${DTB_BASE_NAME}.${DTB_EXT}.bin \
					       ${DEPLOYDIR}/${type}-initramfs-${DTB_BASE_NAME}.${DTB_EXT}-${MACHINE}.bin
				fi
			fi
		done
	done
}
