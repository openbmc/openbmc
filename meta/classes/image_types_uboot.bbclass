inherit image_types kernel-arch

oe_mkimage () {
    mkimage -A ${UBOOT_ARCH} -O linux -T ramdisk -C $2 -n ${IMAGE_NAME} \
        -d ${DEPLOY_DIR_IMAGE}/$1 ${DEPLOY_DIR_IMAGE}/$1.u-boot
    if [ x$3 = x"clean" ]; then
        rm $1
    fi
}

COMPRESSIONTYPES += "gz.u-boot bz2.u-boot lzma.u-boot u-boot"

COMPRESS_DEPENDS_u-boot = "u-boot-mkimage-native"
COMPRESS_CMD_u-boot      = "oe_mkimage ${IMAGE_NAME}.rootfs.${type} none"

COMPRESS_DEPENDS_gz.u-boot = "u-boot-mkimage-native"
COMPRESS_CMD_gz.u-boot      = "${COMPRESS_CMD_gz}; oe_mkimage ${IMAGE_NAME}.rootfs.${type}.gz gzip clean"

COMPRESS_DEPENDS_bz2.u-boot = "u-boot-mkimage-native"
COMPRESS_CMD_bz2.u-boot      = "${COMPRESS_CMD_bz2}; oe_mkimage ${IMAGE_NAME}.rootfs.${type}.bz2 bzip2 clean"

COMPRESS_DEPENDS_lzma.u-boot = "u-boot-mkimage-native"
COMPRESS_CMD_lzma.u-boot      = "${COMPRESS_CMD_lzma}; oe_mkimage ${IMAGE_NAME}.rootfs.${type}.lzma lzma clean"

IMAGE_TYPES += "ext2.u-boot ext2.gz.u-boot ext2.bz2.u-boot ext2.lzma.u-boot ext3.gz.u-boot ext4.gz.u-boot cpio.gz.u-boot"

