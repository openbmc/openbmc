#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Support for device tree generation
python () {
    if not bb.data.inherits_class('nopackages', d):
        d.appendVar("PACKAGES", " ${KERNEL_PACKAGE_NAME}-devicetree")
        if d.getVar('KERNEL_DEVICETREE_BUNDLE') == '1':
            d.appendVar("PACKAGES", " ${KERNEL_PACKAGE_NAME}-image-zimage-bundle")
}

# recursivly search for devicetree files
FILES:${KERNEL_PACKAGE_NAME}-devicetree = " \
    /${KERNEL_DTBDEST}/**/*.dtb \
    /${KERNEL_DTBDEST}/**/*.dtbo \
"

FILES:${KERNEL_PACKAGE_NAME}-image-zimage-bundle = "/${KERNEL_IMAGEDEST}/zImage-*.dtb.bin"

# Generate kernel+devicetree bundle
KERNEL_DEVICETREE_BUNDLE ?= "0"

# dtc flags passed via DTC_FLAGS env variable
KERNEL_DTC_FLAGS ?= ""

normalize_dtb () {
	dtb="$1"
	if echo $dtb | grep -q '/dts/'; then
		bbwarn "$dtb contains the full path to the the dts file, but only the dtb name should be used."
		dtb=`basename $dtb | sed 's,\.dts$,.dtb,g'`
	fi
	echo "$dtb"
}

get_real_dtb_path_in_kernel () {
	dtb="$1"
	dtb_path="${B}/arch/${ARCH}/boot/dts/$dtb"
	if [ ! -e "$dtb_path" ]; then
		dtb_path="${B}/arch/${ARCH}/boot/$dtb"
	fi
	echo "$dtb_path"
}

do_configure:append() {
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

do_compile:append() {
	if [ -n "${KERNEL_DTC_FLAGS}" ]; then
		export DTC_FLAGS="${KERNEL_DTC_FLAGS}"
	fi

	for dtbf in ${KERNEL_DEVICETREE}; do
		dtb=`normalize_dtb "$dtbf"`
		oe_runmake $dtb CC="${KERNEL_CC} $cc_extra " LD="${KERNEL_LD}" OBJCOPY="${KERNEL_OBJCOPY}" STRIP="${KERNEL_STRIP}" ${KERNEL_EXTRA_ARGS}
	done
}

do_install:append() {
	install -d ${D}/${KERNEL_DTBDEST}
	for dtbf in ${KERNEL_DEVICETREE}; do
		dtb=`normalize_dtb "$dtbf"`
		dtb_path=`get_real_dtb_path_in_kernel "$dtb"`
		if "${@'false' if oe.types.boolean(d.getVar('KERNEL_DTBVENDORED')) else 'true'}"; then
			dtb_ext=${dtb##*.}
			dtb_base_name=`basename $dtb .$dtb_ext`
			dtb=$dtb_base_name.$dtb_ext
		fi
		install -Dm 0644 $dtb_path ${D}/${KERNEL_DTBDEST}/$dtb
	done
}

do_deploy:append() {
	for dtbf in ${KERNEL_DEVICETREE}; do
		dtb=`normalize_dtb "$dtbf"`
		dtb_ext=${dtb##*.}
		dtb_base_name=`basename $dtb .$dtb_ext`
		install -d $deployDir
		if "${@'false' if oe.types.boolean(d.getVar('KERNEL_DTBVENDORED')) else 'true'}"; then
			dtb=$dtb_base_name.$dtb_ext
		fi
		install -m 0644 ${D}/${KERNEL_DTBDEST}/$dtb $deployDir/$dtb_base_name.$dtb_ext
		if [ -n "${KERNEL_DTB_NAME}" ] ; then
			ln -sf $dtb_base_name.$dtb_ext $deployDir/$dtb_base_name-${KERNEL_DTB_NAME}.$dtb_ext
		fi
		if [ -n "${KERNEL_DTB_LINK_NAME}" ] ; then
			ln -sf $dtb_base_name.$dtb_ext $deployDir/$dtb_base_name-${KERNEL_DTB_LINK_NAME}.$dtb_ext
		fi
		for type in ${KERNEL_IMAGETYPE_FOR_MAKE}; do
			if [ "$type" = "zImage" ] && [ "${KERNEL_DEVICETREE_BUNDLE}" = "1" ]; then
				cat ${D}/${KERNEL_IMAGEDEST}/$type \
					$deployDir/$dtb_base_name.$dtb_ext \
					> $deployDir/$type-$dtb_base_name.$dtb_ext${KERNEL_DTB_BIN_EXT}
				if [ -n "${KERNEL_DTB_NAME}" ]; then
					ln -sf $type-$dtb_base_name.$dtb_ext${KERNEL_DTB_BIN_EXT} \
						$deployDir/$type-$dtb_base_name-${KERNEL_DTB_NAME}.$dtb_ext${KERNEL_DTB_BIN_EXT}
				fi
				if [ -n "${KERNEL_DTB_LINK_NAME}" ]; then
					ln -sf $type-$dtb_base_name.$dtb_ext${KERNEL_DTB_BIN_EXT} \
						$deployDir/$type-$dtb_base_name-${KERNEL_DTB_LINK_NAME}.$dtb_ext${KERNEL_DTB_BIN_EXT}
				fi
				if [ -e "${KERNEL_OUTPUT_DIR}/${type}.initramfs" ]; then
					cat ${KERNEL_OUTPUT_DIR}/${type}.initramfs \
						$deployDir/$dtb_base_name.$dtb_ext \
						>  $deployDir/${type}-${INITRAMFS_NAME}-$dtb_base_name.$dtb_ext${KERNEL_DTB_BIN_EXT}
					if [ -n "${KERNEL_DTB_NAME}" ]; then
						ln -sf ${type}-${INITRAMFS_NAME}-$dtb_base_name.$dtb_ext${KERNEL_DTB_BIN_EXT} \
							$deployDir/${type}-${INITRAMFS_NAME}-$dtb_base_name-${KERNEL_DTB_NAME}.$dtb_ext${KERNEL_DTB_BIN_EXT}
					fi
					if [ -n "${KERNEL_DTB_LINK_NAME}" ]; then
						ln -sf ${type}-${INITRAMFS_NAME}-$dtb_base_name.$dtb_ext${KERNEL_DTB_BIN_EXT} \
							$deployDir/${type}-${INITRAMFS_NAME}-$dtb_base_name-${KERNEL_DTB_LINK_NAME}.$dtb_ext${KERNEL_DTB_BIN_EXT}
					fi
				fi
			fi
		done
	done
}
