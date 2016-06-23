inherit image_types_uboot

# oe_mkimage() was defined in image_types_uboot. Howver, it does not consider
# the image load address and entry point. Override it here.

oe_mkimage () {
       mkimage -A ${UBOOT_ARCH} -O linux -T ramdisk -C $2 -n ${IMAGE_BASENAME} \
              -a ${INITRD_IMAGE_LOADADDRESS} -e ${INITRD_IMAGE_ENTRYPOINT} \
              -d ${DEPLOY_DIR_IMAGE}/$1 ${DEPLOY_DIR_IMAGE}/$1.u-boot
}

INITRD_IMAGE_ENTRYPOINT ?= "0x40800000"
INITRD_IMAGE_LOADADDRESS ?= "${INITRD_IMAGE_ENTRYPOINT}"
INITRD_LINK_NAME = "${INITRD_IMAGE}-${MACHINE}${INITRAMFS_FSTYPE}"

FLASH_IMAGE_NAME ?= "flash-${MACHINE}-${DATETIME}"
FLASH_IMAGE_NAME[vardepsexclude] = "DATETIME"
FLASH_IMAGE_LINK ?= "flash-${MACHINE}"

FLASH_UBOOT_OFFSET ?= "0"
FLASH_KERNEL_OFFSET ?= "512"
FLASH_INITRD_OFFSET ?= "3072"
FLASH_ROFS_OFFSET ?= "4864"
FLASH_RWFS_OFFSET ?= "28672"
RWFS_SIZE ?= "4096"

# Allow rwfs mkfs configuration through OVERLAY_MKFS_OPTS and OVERRIDES. However,
# avoid setting 'ext4' or 'jffs2' in OVERRIDES as such raw filesystem types are
# reserved for the primary image (and setting them currently breaks the build).
# Instead, prefix the overlay override value with 'rwfs-' to avoid collisions.
DISTROOVERRIDES .= ":rwfs-${OVERLAY_BASETYPE}"

OVERLAY_MKFS_OPTS_rwfs-ext4 = "-b 4096 -F -O^huge_file"

# $(( ${FLASH_SIZE} - ${FLASH_RWFS_OFFSET} ))

# IMAGE_POSTPROCESS_COMMAND += "do_generate_flash"

mk_nor_image() {
       image_dst="$1"
       image_size_kb=$2
       dd if=/dev/zero bs=1k count=${image_size_kb} \
              | tr '\000' '\377' > ${image_dst}
}

do_generate_flash() {
       INITRD_CTYPE=${INITRAMFS_CTYPE}
       ddir="${DEPLOY_DIR_IMAGE}"
       kernel="${KERNEL_IMAGETYPE}"
       uboot="u-boot.${UBOOT_SUFFIX}"
       initrd="${INITRD_LINK_NAME}.cpio.${INITRD_CTYPE}"
       uinitrd="${initrd}.u-boot"
       rootfs="${IMAGE_LINK_NAME}.${IMAGE_BASETYPE}"
       rwfs="rwfs.${OVERLAY_BASETYPE}"

       if [ ! -f $ddir/$kernel ]; then
              bbfatal "Kernel file ${ddir}/${kernel} does not exist"
       fi
       if [ ! -f $ddir/$uboot ]; then
              bbfatal "U-boot file ${ddir}/${uboot} does not exist"
       fi
       if [ ! -f $ddir/$initrd ]; then
              bbfatal "initrd file ${ddir}/${initrd} does not exist"
       fi
       if [ ! -f $ddir/$rootfs ]; then
              bbfatal "Rootfs file ${ddir}/${rootfs} does not exist"
       fi

       oe_mkimage  "${initrd}" "${INITRD_CTYPE}" || bbfatal "oe_mkimage initrd"

       mk_nor_image ${ddir}/${rwfs} ${RWFS_SIZE}
       if [ "${OVERLAY_BASETYPE}" != jffs2 ]; then
              mkfs.${OVERLAY_BASETYPE} ${OVERLAY_MKFS_OPTS} ${ddir}/${rwfs} || \
                     bbfatal "mkfs rwfs"
       fi

       dst="${ddir}/${FLASH_IMAGE_NAME}"
       rm -rf $dst
       mk_nor_image ${dst} ${FLASH_SIZE}
       dd if=${ddir}/${uboot} of=${dst} bs=1k seek=${FLASH_UBOOT_OFFSET}
       dd if=${ddir}/${kernel} of=${dst} bs=1k seek=${FLASH_KERNEL_OFFSET}
       dd if=${ddir}/${uinitrd} of=${dst} bs=1k seek=${FLASH_INITRD_OFFSET}
       dd if=${ddir}/${rootfs} of=${dst} bs=1k seek=${FLASH_ROFS_OFFSET}
       dd if=${ddir}/${rwfs} of=${dst} bs=1k seek=${FLASH_RWFS_OFFSET}
       dstlink="${ddir}/${FLASH_IMAGE_LINK}"
       rm -rf $dstlink
       ln -sf ${FLASH_IMAGE_NAME} $dstlink

       ln -sf ${FLASH_IMAGE_NAME} ${ddir}/image-bmc
       ln -sf ${uboot} ${ddir}/image-u-boot
       ln -sf ${kernel} ${ddir}/image-kernel
       ln -sf ${uinitrd} ${ddir}/image-initramfs
       ln -sf ${rootfs} ${ddir}/image-rofs
       ln -sf ${rwfs} ${ddir}/image-rwfs

       tar -h -cvf ${ddir}/${MACHINE}-${DATETIME}.all.tar -C ${ddir} image-bmc
       tar -h -cvf ${ddir}/${MACHINE}-${DATETIME}.tar -C ${ddir} image-u-boot image-kernel image-initramfs image-rofs image-rwfs
}
do_generate_flash[vardepsexclude] = "DATETIME"
