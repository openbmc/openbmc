# oe_mkimage() was defined in image_types_uboot. Howver, it does not consider
# the image load address and entry point. Override it here. IMGDEPLOYDIR

oe_mkimage_mlx () {
       mkimage -A ${UBOOT_ARCH} -O linux -T ramdisk -C $2 -n ${IMAGE_BASENAME} \
              -a ${INITRD_IMAGE_LOADADDRESS} -e ${INITRD_IMAGE_ENTRYPOINT} \
              -d ${DEPLOY_DIR_IMAGE}/$1 ${DEPLOY_DIR_IMAGE}/$1.u-boot
}

PACKAGES = " "
EXCLUDE_FROM_WORLD = "1"

IMAGE_BASETYPE ?= "squashfs-xz"
OVERLAY_BASETYPE ?= "jffs2"

IMAGE_TYPES += "overlay"

IMAGE_TYPEDEP_overlay = "${IMAGE_BASETYPE}"
IMAGE_TYPES_MASKED += "overlay"

ROOTFS ?= "${DEPLOY_DIR_IMAGE}/${IMAGE_BASENAME}-${MACHINE}.${IMAGE_BASETYPE}"

INITRD_IMAGE_ENTRYPOINT ?= "0x40800000"
INITRD_IMAGE_LOADADDRESS ?= "${INITRD_IMAGE_ENTRYPOINT}"
INITRD_LINK_NAME = "${INITRAMFS_IMAGE}-${MACHINE}${INITRAMFS_FSTYPE}"

FLASH_IMAGE_NAME ?= "flash-${MACHINE}-${DATETIME}"
FLASH_IMAGE_NAME[vardepsexclude] = "DATETIME"
FLASH_IMAGE_LINK ?= "flash-${MACHINE}"

FLASH_KERNEL_IMAGE ?= "${KERNEL_IMAGETYPES}-${MACHINE}.bin"

FLASH_UBOOT_OFFSET ?= "0"
FLASH_UBOOTENV_OFFSET ?= "384"
FLASH_KERNEL_OFFSET ?= "448"
FLASH_DTB_OFFSET ?= "3008"
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

mk_nor_image() {
       image_dst="$1"
       image_size_kb=$2
       dd if=/dev/zero bs=1k count=${image_size_kb} \
              | tr '\000' '\377' > ${image_dst}
}

do_generate_flash() {
       INITRD_CTYPE=${INITRAMFS_CTYPE}
       ddir="${DEPLOY_DIR_IMAGE}"
       kernel="${FLASH_KERNEL_IMAGE}"
       uboot="u-boot.${UBOOT_SUFFIX}"
       ubootenv="u-boot-env.bin"
       dtb="${KERNEL_DEVICETREE}"
       initrd="${INITRD_LINK_NAME}.cpio.${INITRD_CTYPE}"
       uinitrd="${initrd}.u-boot"
       rootfs="${IMAGE_LINK_NAME}.${IMAGE_BASETYPE}"
       rwfs="rwfs.${OVERLAY_BASETYPE}"
       rofsimg=rofs.${IMAGE_BASETYPE}.cpio
       netimg=initramfs-netboot.cpio

       if [ ! -f $ddir/$kernel ]; then
              bbfatal "Kernel file ${ddir}/${kernel} does not exist"
       fi
       if [ ! -f $ddir/$dtb ]; then
              bbfatal "Device tree binary file ${ddir}/${dtb} does not exist"
       fi
       if [ ! -f $ddir/$uboot ]; then
              bbfatal "U-boot file ${ddir}/${uboot} does not exist"
       fi
       if [ ! -f $ddir/$ubootenv ]; then
              bbfatal "U-boot environment file ${ddir}/${ubootenv} does not exist"
       fi
       if [ ! -f $ddir/$initrd ]; then
              bbfatal "initrd file ${ddir}/${initrd} does not exist"
       fi
       if [ ! -f $ddir/$rootfs ]; then
              bbfatal "Rootfs file ${ddir}/${rootfs} does not exist"
       fi
       	   
       oe_mkimage_mlx  "${initrd}" "${INITRD_CTYPE}" || bbfatal "oe_mkimage initrd"
	   
       mk_nor_image ${ddir}/${rwfs} ${RWFS_SIZE}
       if [ "${OVERLAY_BASETYPE}" != jffs2 ]; then
              mkfs.${OVERLAY_BASETYPE} ${OVERLAY_MKFS_OPTS} ${ddir}/${rwfs} || \
                     bbfatal "mkfs rwfs"
       fi

       dst="${ddir}/${FLASH_IMAGE_NAME}"
       rm -rf $dst
       mk_nor_image ${dst} ${FLASH_SIZE}
       dd if=${ddir}/${uboot} of=${dst} bs=1k conv=notrunc seek=${FLASH_UBOOT_OFFSET}
       dd if=${ddir}/${ubootenv} of=${dst} bs=1k conv=notrunc seek=${FLASH_UBOOTENV_OFFSET}
       dd if=${ddir}/${kernel} of=${dst} bs=1k conv=notrunc seek=${FLASH_KERNEL_OFFSET}
       dd if=${ddir}/${dtb} of=${dst} bs=1k conv=notrunc seek=${FLASH_DTB_OFFSET}
       dd if=${ddir}/${uinitrd} of=${dst} bs=1k conv=notrunc seek=${FLASH_INITRD_OFFSET}
       dd if=${ddir}/${rootfs} of=${dst} bs=1k conv=notrunc seek=${FLASH_ROFS_OFFSET}
       dd if=${ddir}/${rwfs} of=${dst} bs=1k conv=notrunc seek=${FLASH_RWFS_OFFSET}
       dstlink="${ddir}/${FLASH_IMAGE_LINK}"
       rm -rf $dstlink
       ln -sf ${FLASH_IMAGE_NAME} $dstlink

       ln -sf ${FLASH_IMAGE_NAME} ${ddir}/image-bmc
       ln -sf ${uboot} ${ddir}/image-u-boot
       ln -sf ${ubootenv} ${ddir}/image-u-boot-env
       ln -sf ${kernel} ${ddir}/image-kernel
       ln -sf ${dtb} ${ddir}/image-dtb
       ln -sf ${uinitrd} ${ddir}/image-initramfs
       ln -sf ${rootfs} ${ddir}/image-rofs
       ln -sf ${rwfs} ${ddir}/image-rwfs

       tar -h -cvf ${ddir}/${MACHINE}-${DATETIME}.all.tar -C ${ddir} image-bmc
       tar -h -cvf ${ddir}/${MACHINE}-${DATETIME}.tar -C ${ddir} image-u-boot image-u-boot-env image-kernel image-dtb image-initramfs image-rofs image-rwfs

       # Package the root image (rofs layer) with the initramfs for net booting.
       # Uses the symlink above to get the desired name in the cpio
       ( cd $ddir && echo image-rofs | cpio -oHnewc -L > ${rofsimg} )
       # Prepend the rofs cpio -- being uncompressed it must be 4-byte aligned
       cat ${ddir}/${rofsimg} ${ddir}/${initrd} > ${ddir}/${netimg}
       oe_mkimage_mlx  "${netimg}" "${INITRD_CTYPE}"

}
do_generate_flash[vardepsexclude] = "DATETIME"

do_generate_flash[depends] += "${PN}:do_image_complete"
do_generate_flash[depends] += "u-boot:do_populate_sysroot"

addtask generate_flash before do_build
