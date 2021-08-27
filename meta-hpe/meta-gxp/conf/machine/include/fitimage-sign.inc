inherit uboot-sign

UBOOT_SIGN_ENABLE = "0"
UBOOT_SIGN_KEYDIR = "${COREBASE}/meta-hpe/meta-gxp/recipes-bsp/u-boot/u-boot-gxp/"
UBOOT_SIGN_KEYNAME = "dev"
FIT_HASH_ALG = "sha512"
UBOOT_FIT_HASH_ALG = "sha512"

concat_dtb_helper:append() {
	if [ -e "${DEPLOYDIR}/${UBOOT_NODTB_IMAGE}" -a -e "$deployed_uboot_dtb_binary" ]; then
		cd ${DEPLOYDIR}
		cat ${UBOOT_NODTB_IMAGE} $deployed_uboot_dtb_binary | tee ${B}/${UBOOT_BINARY} > ${UBOOT_IMAGE}
	fi
}
