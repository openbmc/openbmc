# Copyright (C) 2004, Advanced Micro Devices, Inc.  All Rights Reserved
# Released under the MIT license (see packages/COPYING)

# Creates a bootable image using syslinux, your kernel and an optional
# initrd

#
# End result is two things:
#
# 1. A .hddimg file which is an msdos filesystem containing syslinux, a kernel,
# an initrd and a rootfs image. These can be written to harddisks directly and
# also booted on USB flash disks (write them there with dd).
#
# 2. A CD .iso image

# Boot process is that the initrd will boot and process which label was selected
# in syslinux. Actions based on the label are then performed (e.g. installing to
# an hdd)

# External variables (also used by syslinux.bbclass)
# ${INITRD} - indicates a list of filesystem images to concatenate and use as an initrd (optional)
# ${COMPRESSISO} - Transparent compress ISO, reduce size ~40% if set to 1
# ${NOISO}  - skip building the ISO image if set to 1
# ${NOHDD}  - skip building the HDD image if set to 1
# ${HDDIMG_ID} - FAT image volume-id
# ${ROOTFS} - indicates a filesystem image to include as the root filesystem (optional)

inherit live-vm-common

do_bootimg[depends] += "dosfstools-native:do_populate_sysroot \
                        mtools-native:do_populate_sysroot \
                        cdrtools-native:do_populate_sysroot \
                        virtual/kernel:do_deploy \
                        ${MLPREFIX}syslinux:do_populate_sysroot \
                        syslinux-native:do_populate_sysroot \
                        ${@oe.utils.ifelse(d.getVar('COMPRESSISO', False),'zisofs-tools-native:do_populate_sysroot','')} \
                        ${PN}:do_image_ext4 \
                        "


LABELS_LIVE ?= "boot install"
ROOT_LIVE ?= "root=/dev/ram0"
INITRD_IMAGE_LIVE ?= "core-image-minimal-initramfs"
INITRD_LIVE ?= "${DEPLOY_DIR_IMAGE}/${INITRD_IMAGE_LIVE}-${MACHINE}.cpio.gz"

ROOTFS ?= "${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.ext4"

IMAGE_TYPEDEP_live = "ext4"
IMAGE_TYPEDEP_iso = "ext4"
IMAGE_TYPEDEP_hddimg = "ext4"
IMAGE_TYPES_MASKED += "live hddimg iso"

python() {
    image_b = d.getVar('IMAGE_BASENAME', True)
    initrd_i = d.getVar('INITRD_IMAGE_LIVE', True)
    if image_b == initrd_i:
        bb.error('INITRD_IMAGE_LIVE %s cannot use image live, hddimg or iso.' % initrd_i)
        bb.fatal('Check IMAGE_FSTYPES and INITRAMFS_FSTYPES settings.')
    else:
        d.appendVarFlag('do_bootimg', 'depends', ' %s:do_image_complete' % initrd_i)
}

HDDDIR = "${S}/hddimg"
ISODIR = "${S}/iso"
EFIIMGDIR = "${S}/efi_img"
COMPACT_ISODIR = "${S}/iso.z"
COMPRESSISO ?= "0"

ISOLINUXDIR ?= "/isolinux"
ISO_BOOTIMG = "isolinux/isolinux.bin"
ISO_BOOTCAT = "isolinux/boot.cat"
MKISOFS_OPTIONS = "-no-emul-boot -boot-load-size 4 -boot-info-table"

BOOTIMG_VOLUME_ID   ?= "boot"
BOOTIMG_EXTRA_SPACE ?= "512"

populate_live() {
    populate_kernel $1
	if [ -s "${ROOTFS}" ]; then
		install -m 0644 ${ROOTFS} $1/rootfs.img
	fi
}

build_iso() {
	# Only create an ISO if we have an INITRD and NOISO was not set
	if [ -z "${INITRD}" ] || [ "${NOISO}" = "1" ]; then
		bbnote "ISO image will not be created."
		return
	fi
	# ${INITRD} is a list of multiple filesystem images
	for fs in ${INITRD}
	do
		if [ ! -s "$fs" ]; then
			bbnote "ISO image will not be created. $fs is invalid."
			return
		fi
	done

	populate_live ${ISODIR}

	if [ "${PCBIOS}" = "1" ]; then
		syslinux_iso_populate ${ISODIR}
	fi
	if [ "${EFI}" = "1" ]; then
		efi_iso_populate ${ISODIR}
		build_fat_img ${EFIIMGDIR} ${ISODIR}/efi.img
	fi

	# EFI only
	if [ "${PCBIOS}" != "1" ] && [ "${EFI}" = "1" ] ; then
		# Work around bug in isohybrid where it requires isolinux.bin
		# In the boot catalog, even though it is not used
		mkdir -p ${ISODIR}/${ISOLINUXDIR}
		install -m 0644 ${STAGING_DATADIR}/syslinux/isolinux.bin ${ISODIR}${ISOLINUXDIR}
	fi

	if [ "${COMPRESSISO}" = "1" ] ; then
		# create compact directory, compress iso
		mkdir -p ${COMPACT_ISODIR}
		mkzftree -z 9 -p 4 -F ${ISODIR}/rootfs.img ${COMPACT_ISODIR}/rootfs.img

		# move compact iso to iso, then remove compact directory
		mv ${COMPACT_ISODIR}/rootfs.img ${ISODIR}/rootfs.img
		rm -Rf ${COMPACT_ISODIR}
		mkisofs_compress_opts="-R -z -D -l"
	else
		mkisofs_compress_opts="-r"
	fi

	# Check the size of ${ISODIR}/rootfs.img, use mkisofs -iso-level 3
	# when it exceeds 3.8GB, the specification is 4G - 1 bytes, we need
	# leave a few space for other files.
	mkisofs_iso_level=""

        if [ -n "${ROOTFS}" ] && [ -s "${ROOTFS}" ]; then
		rootfs_img_size=`stat -c '%s' ${ISODIR}/rootfs.img`
		# 4080218931 = 3.8 * 1024 * 1024 * 1024
		if [ $rootfs_img_size -gt 4080218931 ]; then
			bbnote "${ISODIR}/rootfs.img execeeds 3.8GB, using '-iso-level 3' for mkisofs"
			mkisofs_iso_level="-iso-level 3"
		fi
	fi

	if [ "${PCBIOS}" = "1" ] && [ "${EFI}" != "1" ] ; then
		# PCBIOS only media
		mkisofs -V ${BOOTIMG_VOLUME_ID} \
		        -o ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.iso \
			-b ${ISO_BOOTIMG} -c ${ISO_BOOTCAT} \
			$mkisofs_compress_opts \
			${MKISOFS_OPTIONS} $mkisofs_iso_level ${ISODIR}
	else
		# EFI only OR EFI+PCBIOS
		mkisofs -A ${BOOTIMG_VOLUME_ID} -V ${BOOTIMG_VOLUME_ID} \
		        -o ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.iso \
			-b ${ISO_BOOTIMG} -c ${ISO_BOOTCAT} \
			$mkisofs_compress_opts ${MKISOFS_OPTIONS} $mkisofs_iso_level \
			-eltorito-alt-boot -eltorito-platform efi \
			-b efi.img -no-emul-boot \
			${ISODIR}
		isohybrid_args="-u"
	fi

	isohybrid $isohybrid_args ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.iso
}

build_fat_img() {
	FATSOURCEDIR=$1
	FATIMG=$2

	# Calculate the size required for the final image including the
	# data and filesystem overhead.
	# Sectors: 512 bytes
	#  Blocks: 1024 bytes

	# Determine the sector count just for the data
	SECTORS=$(expr $(du --apparent-size -ks ${FATSOURCEDIR} | cut -f 1) \* 2)

	# Account for the filesystem overhead. This includes directory
	# entries in the clusters as well as the FAT itself.
	# Assumptions:
	#   FAT32 (12 or 16 may be selected by mkdosfs, but the extra
	#   padding will be minimal on those smaller images and not
	#   worth the logic here to caclulate the smaller FAT sizes)
	#   < 16 entries per directory
	#   8.3 filenames only

	# 32 bytes per dir entry
	DIR_BYTES=$(expr $(find ${FATSOURCEDIR} | tail -n +2 | wc -l) \* 32)
	# 32 bytes for every end-of-directory dir entry
	DIR_BYTES=$(expr $DIR_BYTES + $(expr $(find ${FATSOURCEDIR} -type d | tail -n +2 | wc -l) \* 32))
	# 4 bytes per FAT entry per sector of data
	FAT_BYTES=$(expr $SECTORS \* 4)
	# 4 bytes per FAT entry per end-of-cluster list
	FAT_BYTES=$(expr $FAT_BYTES + $(expr $(find ${FATSOURCEDIR} -type d | tail -n +2 | wc -l) \* 4))

	# Use a ceiling function to determine FS overhead in sectors
	DIR_SECTORS=$(expr $(expr $DIR_BYTES + 511) / 512)
	# There are two FATs on the image
	FAT_SECTORS=$(expr $(expr $(expr $FAT_BYTES + 511) / 512) \* 2)
	SECTORS=$(expr $SECTORS + $(expr $DIR_SECTORS + $FAT_SECTORS))

	# Determine the final size in blocks accounting for some padding
	BLOCKS=$(expr $(expr $SECTORS / 2) + ${BOOTIMG_EXTRA_SPACE})

	# Ensure total sectors is an integral number of sectors per
	# track or mcopy will complain. Sectors are 512 bytes, and we
	# generate images with 32 sectors per track. This calculation is
	# done in blocks, thus the mod by 16 instead of 32.
	BLOCKS=$(expr $BLOCKS + $(expr 16 - $(expr $BLOCKS % 16)))

	# mkdosfs will sometimes use FAT16 when it is not appropriate,
	# resulting in a boot failure from SYSLINUX. Use FAT32 for
	# images larger than 512MB, otherwise let mkdosfs decide.
	if [ $(expr $BLOCKS / 1024) -gt 512 ]; then
		FATSIZE="-F 32"
	fi

	# mkdosfs will fail if ${FATIMG} exists. Since we are creating an
	# new image, it is safe to delete any previous image.
	if [ -e ${FATIMG} ]; then
		rm ${FATIMG}
	fi

	if [ -z "${HDDIMG_ID}" ]; then
		mkdosfs ${FATSIZE} -n ${BOOTIMG_VOLUME_ID} -S 512 -C ${FATIMG} \
			${BLOCKS}
	else
		mkdosfs ${FATSIZE} -n ${BOOTIMG_VOLUME_ID} -S 512 -C ${FATIMG} \
		${BLOCKS} -i ${HDDIMG_ID}
	fi

	# Copy FATSOURCEDIR recursively into the image file directly
	mcopy -i ${FATIMG} -s ${FATSOURCEDIR}/* ::/
}

build_hddimg() {
	# Create an HDD image
	if [ "${NOHDD}" != "1" ] ; then
		populate_live ${HDDDIR}

		if [ "${PCBIOS}" = "1" ]; then
			syslinux_hddimg_populate ${HDDDIR}
		fi
		if [ "${EFI}" = "1" ]; then
			efi_hddimg_populate ${HDDDIR}
		fi

		# Check the size of ${HDDDIR}/rootfs.img, error out if it
		# exceeds 4GB, it is the single file's max size of FAT fs.
		if [ -f ${HDDDIR}/rootfs.img ]; then
			rootfs_img_size=`stat -c '%s' ${HDDDIR}/rootfs.img`
			max_size=`expr 4 \* 1024 \* 1024 \* 1024`
			if [ $rootfs_img_size -gt $max_size ]; then
				bberror "${HDDDIR}/rootfs.img execeeds 4GB,"
				bberror "this doesn't work on FAT filesystem, you can try either of:"
				bberror "1) Reduce the size of rootfs.img"
				bbfatal "2) Use iso, vmdk or vdi to instead of hddimg\n"
			fi
		fi

		build_fat_img ${HDDDIR} ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.hddimg

		if [ "${PCBIOS}" = "1" ]; then
			syslinux_hddimg_install
		fi

		chmod 644 ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.hddimg
	fi
}

python do_bootimg() {
    set_live_vm_vars(d, 'LIVE')
    if d.getVar("PCBIOS", True) == "1":
        bb.build.exec_func('build_syslinux_cfg', d)
    if d.getVar("EFI", True) == "1":
        bb.build.exec_func('build_efi_cfg', d)
    bb.build.exec_func('build_hddimg', d)
    bb.build.exec_func('build_iso', d)
    bb.build.exec_func('create_symlinks', d)
}
do_bootimg[subimages] = "hddimg iso"
do_bootimg[imgsuffix] = "."

addtask bootimg before do_image_complete
