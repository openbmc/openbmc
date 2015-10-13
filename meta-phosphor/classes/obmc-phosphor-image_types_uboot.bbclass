inherit image_types_uboot

# oe_mkimage() was defined in image_types_uboot. Howver, it does not consider
# the image load address and entry point. Override it here.

oe_mkimage () {
    mkimage -A ${UBOOT_ARCH} -O linux -T ramdisk -C $2 -n ${IMAGE_NAME} \
        -a ${INITRD_IMAGE_LOADADDRESS} -e ${INITRD_IMAGE_ENTRYPOINT} \
        -d ${DEPLOY_DIR_IMAGE}/$1 ${DEPLOY_DIR_IMAGE}/$1.u-boot
}

INITRD_IMAGE_ENTRYPOINT ?= "0x40800000"
INITRD_IMAGE_LOADADDRESS ?= "${INITRD_IMAGE_ENTRYPOINT}"

FLASH_IMAGE_NAME ?= "flash-${MACHINE}-${DATETIME}"
FLASH_IMAGE_LINK ?= "flash-${MACHINE}"

FLASH_UBOOT_OFFSET ?= "0"
FLASH_KERNEL_OFFSET ?= "512"
FLASH_ROOTFS_OFFSET ?= "3072"

IMAGE_POSTPROCESS_COMMAND += "do_generate_flash"

do_generate_flash() {
       kernel="${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}"
       uboot="${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX}"
       rootfs="${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.cpio.${IMAGE_CTYPE}.u-boot"
       if [ ! -f $kernel ]; then
              bbfatal "Kernel file ${kernel} does not exist"
       fi
       if [ ! -f $uboot ]; then
              bbfatal "U-boot file ${uboot} does not exist"
       fi
       if [ ! -f $rootfs ]; then
              bbfatal "Rootfs file ${rootfs} does not exist"
       fi

       dst="${DEPLOY_DIR_IMAGE}/${FLASH_IMAGE_NAME}"
       rm -rf $dst
       dd if=/dev/zero of=${dst} bs=1k count=${FLASH_SIZE}
       dd if=${uboot} of=${dst} bs=1k seek=${FLASH_UBOOT_OFFSET}
       dd if=${kernel} of=${dst} bs=1k seek=${FLASH_KERNEL_OFFSET}
       dd if=${rootfs} of=${dst} bs=1k seek=${FLASH_ROOTFS_OFFSET}
       dstlink="${DEPLOY_DIR_IMAGE}/${FLASH_IMAGE_LINK}"
       rm -rf $dstlink
       ln -sf ${FLASH_IMAGE_NAME} $dstlink
}
