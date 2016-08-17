inherit kernel-uboot

python __anonymous () {
    kerneltype = d.getVar('KERNEL_IMAGETYPE', True)
    if kerneltype == 'fitImage':
        depends = d.getVar("DEPENDS", True)
        depends = "%s u-boot-mkimage-native dtc-native" % depends
        d.setVar("DEPENDS", depends)

	# Override KERNEL_IMAGETYPE_FOR_MAKE variable, which is internal
	# to kernel.bbclass . We have to override it, since we pack zImage
	# (at least for now) into the fitImage .
        d.setVar("KERNEL_IMAGETYPE_FOR_MAKE", "zImage")

        image = d.getVar('INITRAMFS_IMAGE', True)
        if image:
            d.appendVarFlag('do_assemble_fitimage', 'depends', ' ${INITRAMFS_IMAGE}:do_image_complete')
}

#
# Emit the fitImage ITS header
#
fitimage_emit_fit_header() {
	cat << EOF >> fit-image.its
/dts-v1/;

/ {
        description = "U-Boot fitImage for ${DISTRO_NAME}/${PV}/${MACHINE}";
        #address-cells = <1>;
EOF
}

#
# Emit the fitImage section bits
#
# $1 ... Section bit type: imagestart - image section start
#                          confstart  - configuration section start
#                          sectend    - section end
#                          fitend     - fitimage end
#
fitimage_emit_section_maint() {
	case $1 in
	imagestart)
		cat << EOF >> fit-image.its

        images {
EOF
	;;
	confstart)
		cat << EOF >> fit-image.its

        configurations {
EOF
	;;
	sectend)
		cat << EOF >> fit-image.its
	};
EOF
	;;
	fitend)
		cat << EOF >> fit-image.its
};
EOF
	;;
	esac
}

#
# Emit the fitImage ITS kernel section
#
# $1 ... Image counter
# $2 ... Path to kernel image
# $3 ... Compression type
fitimage_emit_section_kernel() {

	kernel_csum="sha1"

	ENTRYPOINT=${UBOOT_ENTRYPOINT}
	if test -n "${UBOOT_ENTRYSYMBOL}"; then
		ENTRYPOINT=`${HOST_PREFIX}nm ${S}/vmlinux | \
			awk '$3=="${UBOOT_ENTRYSYMBOL}" {print $1}'`
	fi

	cat << EOF >> fit-image.its
                kernel@${1} {
                        description = "Linux kernel";
                        data = /incbin/("${2}");
                        type = "kernel";
                        arch = "${UBOOT_ARCH}";
                        os = "linux";
                        compression = "${3}";
                        load = <${UBOOT_LOADADDRESS}>;
                        entry = <${ENTRYPOINT}>;
                        hash@1 {
                                algo = "${kernel_csum}";
                        };
                };
EOF
}

#
# Emit the fitImage ITS DTB section
#
# $1 ... Image counter
# $2 ... Path to DTB image
fitimage_emit_section_dtb() {

	dtb_csum="sha1"

	cat << EOF >> fit-image.its
                fdt@${1} {
                        description = "Flattened Device Tree blob";
                        data = /incbin/("${2}");
                        type = "flat_dt";
                        arch = "${UBOOT_ARCH}";
                        compression = "none";
                        hash@1 {
                                algo = "${dtb_csum}";
                        };
                };
EOF
}

#
# Emit the fitImage ITS configuration section
#
# $1 ... Linux kernel ID
# $2 ... DTB image ID
fitimage_emit_section_config() {

	conf_csum="sha1"

	# Test if we have any DTBs at all
	if [ -z "${2}" ] ; then
		conf_desc="Boot Linux kernel"
		fdt_line=""
	else
		conf_desc="Boot Linux kernel with FDT blob"
		fdt_line="fdt = \"fdt@${2}\";"
	fi
	kernel_line="kernel = \"kernel@${1}\";"

	cat << EOF >> fit-image.its
                default = "conf@1";
                conf@1 {
                        description = "${conf_desc}";
			${kernel_line}
			${fdt_line}
                        hash@1 {
                                algo = "${conf_csum}";
                        };
                };
EOF
}

do_assemble_fitimage() {
	if test "x${KERNEL_IMAGETYPE}" = "xfitImage" ; then
		kernelcount=1
		dtbcount=""
		rm -f fit-image.its

		fitimage_emit_fit_header

		#
		# Step 1: Prepare a kernel image section.
		#
		fitimage_emit_section_maint imagestart

		uboot_prep_kimage
		fitimage_emit_section_kernel "${kernelcount}" linux.bin "${linux_comp}"

		#
		# Step 2: Prepare a DTB image section
		#
		if test -n "${KERNEL_DEVICETREE}"; then
			dtbcount=1
			for DTB in ${KERNEL_DEVICETREE}; do
				if echo ${DTB} | grep -q '/dts/'; then
					bbwarn "${DTB} contains the full path to the the dts file, but only the dtb name should be used."
					DTB=`basename ${DTB} | sed 's,\.dts$,.dtb,g'`
				fi
				DTB_PATH="arch/${ARCH}/boot/dts/${DTB}"
				if [ ! -e "${DTB_PATH}" ]; then
					DTB_PATH="arch/${ARCH}/boot/${DTB}"
				fi

				fitimage_emit_section_dtb ${dtbcount} ${DTB_PATH}
				dtbcount=`expr ${dtbcount} + 1`
			done
		fi

		fitimage_emit_section_maint sectend

		# Force the first Kernel and DTB in the default config
		kernelcount=1
		dtbcount=1

		#
		# Step 3: Prepare a configurations section
		#
		fitimage_emit_section_maint confstart

		fitimage_emit_section_config ${kernelcount} ${dtbcount}

		fitimage_emit_section_maint sectend

		fitimage_emit_section_maint fitend

		#
		# Step 4: Assemble the image
		#
		uboot-mkimage -f fit-image.its arch/${ARCH}/boot/fitImage
	fi
}

addtask assemble_fitimage before do_install after do_compile

kernel_do_deploy[vardepsexclude] = "DATETIME"
kernel_do_deploy_append() {
	# Update deploy directory
	if test "x${KERNEL_IMAGETYPE}" = "xfitImage" ; then
		cd ${B}
		echo "Copying fit-image.its source file..."
		its_base_name="${KERNEL_IMAGETYPE}-its-${PV}-${PR}-${MACHINE}-${DATETIME}"
		its_symlink_name=${KERNEL_IMAGETYPE}-its-${MACHINE}
		install -m 0644 fit-image.its ${DEPLOYDIR}/${its_base_name}.its
		linux_bin_base_name="${KERNEL_IMAGETYPE}-linux.bin-${PV}-${PR}-${MACHINE}-${DATETIME}"
		linux_bin_symlink_name=${KERNEL_IMAGETYPE}-linux.bin-${MACHINE}
		install -m 0644 linux.bin ${DEPLOYDIR}/${linux_bin_base_name}.bin

		cd ${DEPLOYDIR}
		ln -sf ${its_base_name}.its ${its_symlink_name}.its
		ln -sf ${linux_bin_base_name}.bin ${linux_bin_symlink_name}.bin
	fi
}
