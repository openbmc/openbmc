#
# Assemble fitImage
#
# $1 ... .its filename
# $2 ... fitImage name
# $3 ... include ramdisk
fitimage_assemble_npcm8xx() {
	kernelcount=1
	dtbcount=""
	DTBS=""
	ramdiskcount=${3}
	setupcount=""
	bootscr_id=""
	rm -f ${1} arch/${ARCH}/boot/${2}
	final_offset=600

	fitimage_emit_fit_header ${1}

	#
	# Step 1: Prepare a image section.
	#
	fitimage_emit_section_maint ${1} imagestart

	#
	# Step 2: Prepare a DTB image section
	#

	if [ -n "${KERNEL_DEVICETREE}" ]; then
		dtbcount=1
		for DTB in ${KERNEL_DEVICETREE}; do
			if echo ${DTB} | grep -q '/dts/'; then
				bbwarn "${DTB} contains the full path to the the dts file, but only the dtb name should be used."
				DTB=`basename ${DTB} | sed 's,\.dts$,.dtb,g'`
			fi

			# Skip ${DTB} if it's also provided in ${EXTERNAL_KERNEL_DEVICETREE}
			if [ -n "${EXTERNAL_KERNEL_DEVICETREE}" ] && [ -s ${EXTERNAL_KERNEL_DEVICETREE}/${DTB} ]; then
				continue
			fi

			DTB_PATH="arch/${ARCH}/boot/dts/${DTB}"
			if [ ! -e "${DTB_PATH}" ]; then
				DTB_PATH="arch/${ARCH}/boot/${DTB}"
			fi

			DTB=$(echo "${DTB}" | tr '/' '_')
			DTBS="${DTBS} ${DTB}"
			fitimage_emit_section_dtb ${1} ${DTB} ${DTB_PATH}
		done
	fi

	if [ -n "${EXTERNAL_KERNEL_DEVICETREE}" ]; then
		dtbcount=1
		for DTB in $(find "${EXTERNAL_KERNEL_DEVICETREE}" \( -name '*.dtb' -o -name '*.dtbo' \) -printf '%P\n' | sort); do
			DTB=$(echo "${DTB}" | tr '/' '_')
			DTBS="${DTBS} ${DTB}"
			fitimage_emit_section_dtb ${1} ${DTB} "${EXTERNAL_KERNEL_DEVICETREE}/${DTB}"
		done
	fi

	#
	# Step 3: Prepare a kernel image section.
	#

	uboot_prep_kimage

	if [ "${INITRAMFS_IMAGE_BUNDLE}" = "1" ]; then
		initramfs_bundle_path="arch/"${UBOOT_ARCH}"/boot/"${KERNEL_IMAGETYPE_REPLACEMENT}".initramfs"
		if [ -e "${initramfs_bundle_path}" ]; then

			#
			# Include the kernel/rootfs bundle.
			#

			fitimage_emit_section_kernel ${1} "${kernelcount}" "${initramfs_bundle_path}" "${linux_comp}"
		else
			bbwarn "${initramfs_bundle_path} not found."
		fi
	else
		fitimage_emit_section_kernel ${1} "${kernelcount}" linux.bin "${linux_comp}"
	fi

	#
	# Step 4: Prepare a u-boot script section
	#

	if [ -n "${UBOOT_ENV}" ] && [ -d "${STAGING_DIR_HOST}/boot" ]; then
		if [ -e "${STAGING_DIR_HOST}/boot/${UBOOT_ENV_BINARY}" ]; then
			cp ${STAGING_DIR_HOST}/boot/${UBOOT_ENV_BINARY} ${B}
			bootscr_id="${UBOOT_ENV_BINARY}"
			fitimage_emit_section_boot_script ${1} "${bootscr_id}" ${UBOOT_ENV_BINARY}
		else
			bbwarn "${STAGING_DIR_HOST}/boot/${UBOOT_ENV_BINARY} not found."
		fi
	fi
	#
	# Step 5: Prepare a setup section. (For x86)
	#
	if [ -e arch/${ARCH}/boot/setup.bin ]; then
		setupcount=1
		fitimage_emit_section_setup ${1} "${setupcount}" arch/${ARCH}/boot/setup.bin
	fi

	#
	# Step 6: Prepare a ramdisk section.
	#
	if [ "x${ramdiskcount}" = "x1" ] && [ "${INITRAMFS_IMAGE_BUNDLE}" != "1" ]; then
		# Find and use the first initramfs image archive type we find
		for img in cpio.lz4 cpio.lzo cpio.lzma cpio.xz cpio.gz ext2.gz cpio; do
			initramfs_path="${DEPLOY_DIR_IMAGE}/${INITRAMFS_IMAGE_NAME}.${img}"
			echo "Using $initramfs_path"
			if [ -e "${initramfs_path}" ]; then
				fitimage_emit_section_ramdisk ${1} "${ramdiskcount}" "${initramfs_path}"
				break
			fi
		done
	fi

	fitimage_emit_section_maint ${1} sectend

	# Force the first Kernel and DTB in the default config
	kernelcount=1
	if [ -n "${dtbcount}" ]; then
		dtbcount=1
	fi

	#
	# Step 7: Prepare a configurations section
	#
	fitimage_emit_section_maint ${1} confstart

	# kernel-fitimage.bbclass currently only supports a single kernel (no less or
	# more) to be added to the FIT image along with 0 or more device trees and
	# 0 or 1 ramdisk.
        # It is also possible to include an initramfs bundle (kernel and rootfs in one binary)
        # When the initramfs bundle is used ramdisk is disabled.
	# If a device tree is to be part of the FIT image, then select
	# the default configuration to be used is based on the dtbcount. If there is
	# no dtb present than select the default configuation to be based on
	# the kernelcount.
	if [ -n "${DTBS}" ]; then
		i=1
		for DTB in ${DTBS}; do
			dtb_ext=${DTB##*.}
			if [ "${dtb_ext}" = "dtbo" ]; then
				fitimage_emit_section_config ${1} "" "${DTB}" "" "${bootscr_id}" "" "`expr ${i} = ${dtbcount}`"
			else
				fitimage_emit_section_config ${1} "${kernelcount}" "${DTB}" "${ramdiskcount}" "${bootscr_id}" "${setupcount}" "`expr ${i} = ${dtbcount}`"
			fi
			i=`expr ${i} + 1`
		done
	else
		defaultconfigcount=1
		fitimage_emit_section_config ${1} "${kernelcount}" "" "${ramdiskcount}" "${bootscr_id}"  "${setupcount}" "${defaultconfigcount}"
	fi

	fitimage_emit_section_maint ${1} sectend

	fitimage_emit_section_maint ${1} fitend

	#
	# Step 8: Assemble the image
	#
	${UBOOT_MKIMAGE} -E -p ${final_offset} \
		${@'-D "${UBOOT_MKIMAGE_DTCOPTS}"' if len('${UBOOT_MKIMAGE_DTCOPTS}') else ''} \
		-f ${1} \
		arch/${ARCH}/boot/${2}

	#
	# Step 9: Sign the image and add public key to U-Boot dtb
	#
	if [ "x${UBOOT_SIGN_ENABLE}" = "x1" ] ; then
		add_key_to_u_boot=""
		if [ -n "${UBOOT_DTB_BINARY}" ]; then
			# The u-boot.dtb is a symlink to UBOOT_DTB_IMAGE, so we need copy
			# both of them, and don't dereference the symlink.
			cp -P ${STAGING_DATADIR}/u-boot*.dtb ${B}
			add_key_to_u_boot="-K ${B}/${UBOOT_DTB_BINARY}"
		fi
		${UBOOT_MKIMAGE_SIGN} -E -p ${final_offset} \
			${@'-D "${UBOOT_MKIMAGE_DTCOPTS}"' if len('${UBOOT_MKIMAGE_DTCOPTS}') else ''} \
			-F -k "${UBOOT_SIGN_KEYDIR}" \
			$add_key_to_u_boot \
			-r arch/${ARCH}/boot/${2} \
			${UBOOT_MKIMAGE_SIGN_ARGS}
	fi
}

do_assemble_fitimage:append() {
	if echo ${KERNEL_IMAGETYPES} | grep -wq "fitImage"; then
		cd ${B}
		fitimage_assemble_npcm8xx fit-image.its fitImage
	fi
}

do_assemble_fitimage_initramfs:append() {
	if echo ${KERNEL_IMAGETYPES} | grep -wq "fitImage" && \
		test -n "${INITRAMFS_IMAGE}" ; then
		cd ${B}
		if [ "${INITRAMFS_IMAGE_BUNDLE}" = "1" ]; then
			fitimage_assemble_npcm8xx fit-image-${INITRAMFS_IMAGE}.its fitImage ""
		else
			fitimage_assemble_npcm8xx fit-image-${INITRAMFS_IMAGE}.its fitImage-${INITRAMFS_IMAGE} 1
		fi
	fi
}
