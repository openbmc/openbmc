# This file is part of U-Boot verified boot support and is intended to be
# inherited from u-boot recipe and from kernel-fitimage.bbclass.
#
# The signature procedure requires the user to generate an RSA key and
# certificate in a directory and to define the following variable:
#
#   UBOOT_SIGN_KEYDIR = "/keys/directory"
#   UBOOT_SIGN_KEYNAME = "dev" # keys name in keydir (eg. "dev.crt", "dev.key")
#   UBOOT_MKIMAGE_DTCOPTS = "-I dts -O dtb -p 2000"
#   UBOOT_SIGN_ENABLE = "1"
#
# As verified boot depends on fitImage generation, following is also required:
#
#   KERNEL_CLASSES ?= " kernel-fitimage "
#   KERNEL_IMAGETYPE ?= "fitImage"
#
# The signature support is limited to the use of CONFIG_OF_SEPARATE in U-Boot.
#
# The tasks sequence is set as below, using DEPLOY_IMAGE_DIR as common place to
# treat the device tree blob:
#
#   u-boot:do_deploy_dtb
#   u-boot:do_deploy
#   virtual/kernel:do_assemble_fitimage
#   u-boot:do_concat_dtb
#   u-boot:do_install
#
# For more details on signature process, please refer to U-boot documentation.

# Signature activation.
UBOOT_SIGN_ENABLE ?= "0"

# Default value for deployment filenames.
UBOOT_DTB_IMAGE ?= "u-boot-${MACHINE}-${PV}-${PR}.dtb"
UBOOT_DTB_BINARY ?= "u-boot.dtb"
UBOOT_DTB_SYMLINK ?= "u-boot-${MACHINE}.dtb"
UBOOT_NODTB_IMAGE ?= "u-boot-nodtb-${MACHINE}-${PV}-${PR}.${UBOOT_SUFFIX}"
UBOOT_NODTB_BINARY ?= "u-boot-nodtb.${UBOOT_SUFFIX}"
UBOOT_NODTB_SYMLINK ?= "u-boot-nodtb-${MACHINE}.${UBOOT_SUFFIX}"

#
# Following is relevant only for u-boot recipes:
#

do_concat_dtb () {
	# Concatenate U-Boot w/o DTB & DTB with public key
	# (cf. kernel-fitimage.bbclass for more details)
	cd ${DEPLOYDIR}
	if [ "x${UBOOT_SIGN_ENABLE}" = "x1" ]; then
		if [ -e "${UBOOT_NODTB_IMAGE}" -a -e "${UBOOT_DTB_IMAGE}" ]; then
			cat ${UBOOT_NODTB_IMAGE} ${UBOOT_DTB_IMAGE} | tee ${B}/${UBOOT_BINARY} > ${UBOOT_IMAGE}
		else
			bbwarn "Failure while adding public key to u-boot binary. Verified boot won't be available."
		fi
	fi
}

python () {
	uboot_pn = d.getVar('PREFERRED_PROVIDER_u-boot', True) or 'u-boot'
	if d.getVar('UBOOT_SIGN_ENABLE', True) == '1' and d.getVar('PN', True) == uboot_pn:
		kernel_pn = d.getVar('PREFERRED_PROVIDER_virtual/kernel', True)

		# do_concat_dtb is scheduled _before_ do_install as it overwrite the
		# u-boot.bin in both DEPLOYDIR and DEPLOY_IMAGE_DIR.
		bb.build.addtask('do_concat_dtb', 'do_install', None, d)
		d.appendVarFlag('do_concat_dtb', 'depends', ' %s:do_assemble_fitimage' % kernel_pn)
}
