# Constructs a bootable, fixed-offset mtd image with u-boot
# bootloader, kernel fitimage, read only root filesystem,
# and writeable overlay filesystem.

IMAGE_BASETYPE ?= "squashfs-xz"
OVERLAY_BASETYPE ?= "jffs2"

IMAGE_TYPES += "overlay"

IMAGE_TYPEDEP_overlay = "${IMAGE_BASETYPE}"
IMAGE_TYPES_MASKED += "overlay"

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
	dd if=/dev/zero bs=1k count=$image_size_kb \
		| tr '\000' '\377' > $image_dst
}

do_generate_flash() {
	ddir="${IMGDEPLOYDIR}"
	kernel="${FLASH_KERNEL_IMAGE}"
	uboot="u-boot.${UBOOT_SUFFIX}"
	rootfs="${IMAGE_LINK_NAME}.${IMAGE_BASETYPE}"
	rwfs="rwfs.${OVERLAY_BASETYPE}"

	flash="${IMAGE_NAME}.overlay"
	alltar="${IMAGE_NAME}.all.tar"
	tar="${IMAGE_NAME}.tar"

	mk_nor_image ${S}/$rwfs ${RWFS_SIZE}
	if [ "${OVERLAY_BASETYPE}" != jffs2 ]; then
		mkfs.${OVERLAY_BASETYPE} ${OVERLAY_MKFS_OPTS} ${S}/$rwfs || \
			bbfatal "mkfs rwfs"
	fi

	# Assemble the flash image
	dst="$ddir/$flash"
	mk_nor_image $dst ${FLASH_SIZE}
	dd if=${DEPLOY_DIR_IMAGE}/$uboot of=$dst bs=1k conv=notrunc seek=${FLASH_UBOOT_OFFSET}
	dd if=${DEPLOY_DIR_IMAGE}/$kernel of=$dst bs=1k conv=notrunc seek=${FLASH_KERNEL_OFFSET}
	dd if=$ddir/$rootfs of=$dst bs=1k conv=notrunc seek=${FLASH_ROFS_OFFSET}
	dd if=${S}/$rwfs of=$dst bs=1k conv=notrunc seek=${FLASH_RWFS_OFFSET}

	# Create some links to help make the tar archives
	ln -sf $ddir/${IMAGE_LINK_NAME}.overlay ${S}/image-bmc
	ln -sf ${DEPLOY_DIR_IMAGE}/$uboot ${S}/image-u-boot
	ln -sf ${DEPLOY_DIR_IMAGE}/$kernel ${S}/image-kernel
	ln -sf $ddir/$rootfs ${S}/image-rofs
	ln -sf $rwfs ${S}/image-rwfs

	# Create the tar archives
	tar -h -cvf $ddir/$alltar -C ${S} image-bmc
	tar -h -cvf $ddir/$tar -C ${S} image-u-boot image-kernel image-rofs image-rwfs

	cd ${IMGDEPLOYDIR}
	ln -sf $flash ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.overlay
	ln -sf $alltar ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.all.tar
	ln -sf $tar ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.tar

	# Maintain a number of non-standard name legacy links.
	ln -sf $flash ${IMGDEPLOYDIR}/flash-${MACHINE}
	ln -sf $tar ${IMGDEPLOYDIR}/${MACHINE}-${DATETIME}.tar
	ln -sf $alltar ${IMGDEPLOYDIR}/${MACHINE}-${DATETIME}.all.tar
}
do_generate_flash[vardepsexclude] = "DATETIME"

do_generate_flash[depends] += " \
        ${PN}:do_image_${@d.getVar('IMAGE_BASETYPE', True).replace('-', '_')} \
        virtual/kernel:do_deploy \
        u-boot:do_populate_sysroot \
        "

addtask generate_flash before do_image_complete
