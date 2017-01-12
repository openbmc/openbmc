PACKAGES = " "
EXCLUDE_FROM_WORLD = "1"

IMAGE_BASETYPE ?= "squashfs-xz"
OVERLAY_BASETYPE ?= "jffs2"

IMAGE_TYPES += "overlay"

IMAGE_TYPEDEP_overlay = "${IMAGE_BASETYPE}"
IMAGE_TYPES_MASKED += "overlay"

ROOTFS ?= "${DEPLOY_DIR_IMAGE}/${IMAGE_BASENAME}-${MACHINE}.${IMAGE_BASETYPE}"

FLASH_IMAGE_NAME ?= "flash-${MACHINE}-${DATETIME}"
FLASH_IMAGE_NAME[vardepsexclude] = "DATETIME"
FLASH_IMAGE_LINK ?= "flash-${MACHINE}"

FLASH_KERNEL_IMAGE ?= "fitImage-${INITRAMFS_IMAGE}-${MACHINE}.bin"

FLASH_UBOOT_OFFSET ?= "0"
FLASH_KERNEL_OFFSET ?= "512"
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
       ddir="${DEPLOY_DIR_IMAGE}"
       kernel="${FLASH_KERNEL_IMAGE}"
       uboot="u-boot.${UBOOT_SUFFIX}"
       rootfs="${IMAGE_LINK_NAME}.${IMAGE_BASETYPE}"
       rwfs="rwfs.${OVERLAY_BASETYPE}"

       if [ ! -f $ddir/$kernel ]; then
              bbfatal "Kernel file ${ddir}/${kernel} does not exist"
       fi
       if [ ! -f $ddir/$uboot ]; then
              bbfatal "U-boot file ${ddir}/${uboot} does not exist"
       fi
       if [ ! -f $ddir/$rootfs ]; then
              bbfatal "Rootfs file ${ddir}/${rootfs} does not exist"
       fi

       mk_nor_image ${ddir}/${rwfs} ${RWFS_SIZE}
       if [ "${OVERLAY_BASETYPE}" != jffs2 ]; then
              mkfs.${OVERLAY_BASETYPE} ${OVERLAY_MKFS_OPTS} ${ddir}/${rwfs} || \
                     bbfatal "mkfs rwfs"
       fi

       dst="${ddir}/${FLASH_IMAGE_NAME}"
       rm -rf $dst
       mk_nor_image ${dst} ${FLASH_SIZE}
       dd if=${ddir}/${uboot} of=${dst} bs=1k conv=notrunc seek=${FLASH_UBOOT_OFFSET}
       dd if=${ddir}/${kernel} of=${dst} bs=1k conv=notrunc seek=${FLASH_KERNEL_OFFSET}
       dd if=${ddir}/${rootfs} of=${dst} bs=1k conv=notrunc seek=${FLASH_ROFS_OFFSET}
       dd if=${ddir}/${rwfs} of=${dst} bs=1k conv=notrunc seek=${FLASH_RWFS_OFFSET}
       dstlink="${ddir}/${FLASH_IMAGE_LINK}"
       rm -rf $dstlink
       ln -sf ${FLASH_IMAGE_NAME} $dstlink

       ln -sf ${FLASH_IMAGE_NAME} ${ddir}/image-bmc
       ln -sf ${uboot} ${ddir}/image-u-boot
       ln -sf ${kernel} ${ddir}/image-kernel
       ln -sf ${rootfs} ${ddir}/image-rofs
       ln -sf ${rwfs} ${ddir}/image-rwfs

       tar -h -cvf ${ddir}/${MACHINE}-${DATETIME}.all.tar -C ${ddir} image-bmc
       tar -h -cvf ${ddir}/${MACHINE}-${DATETIME}.tar -C ${ddir} image-u-boot image-kernel image-rofs image-rwfs
}
do_generate_flash[vardepsexclude] = "DATETIME"

do_generate_flash[depends] += "${PN}:do_image_complete"
do_generate_flash[depends] += "u-boot:do_populate_sysroot"

addtask generate_flash before do_build
