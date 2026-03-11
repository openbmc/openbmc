#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Generate and deploy additional artifacts required for FIT image creation.
# To use this class, add it to the KERNEL_CLASSES variable.

inherit kernel-uboot

kernel_do_deploy:append() {
	# Provide the kernel artifacts to post processing recipes e.g. for creating a FIT image
	uboot_prep_kimage "$deployDir"
	# For x86 a setup.bin needs to be include"d in a fitImage as well
	if [ -e ${KERNEL_OUTPUT_DIR}/setup.bin ]; then
		install -D "${B}/${KERNEL_OUTPUT_DIR}/setup.bin" "$deployDir/"
	fi
}
