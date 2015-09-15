# boot-directdisk.bbclass
# (loosly based off bootimg.bbclass Copyright (C) 2004, Advanced Micro Devices, Inc.)
#
# Create an image which can be placed directly onto a harddisk using dd and then
# booted.
#
# This uses syslinux. extlinux would have been nice but required the ext2/3 
# partition to be mounted. grub requires to run itself as part of the install 
# process.
#
# The end result is a 512 boot sector populated with an MBR and partition table
# followed by an msdos fat16 partition containing syslinux and a linux kernel
# completed by the ext2/3 rootfs.
#
# We have to push the msdos parition table size > 16MB so fat 16 is used as parted
# won't touch fat12 partitions.

# External variables needed

# ${ROOTFS} - the rootfs image to incorporate

do_bootdirectdisk[depends] += "dosfstools-native:do_populate_sysroot \
                               virtual/kernel:do_deploy \
                               syslinux:do_populate_sysroot \
                               syslinux-native:do_populate_sysroot \
                               parted-native:do_populate_sysroot \
                               mtools-native:do_populate_sysroot "

PACKAGES = " "
EXCLUDE_FROM_WORLD = "1"

BOOTDD_VOLUME_ID   ?= "boot"
BOOTDD_EXTRA_SPACE ?= "16384"

EFI = "${@bb.utils.contains("MACHINE_FEATURES", "efi", "1", "0", d)}"
EFI_PROVIDER ?= "grub-efi"
EFI_CLASS = "${@bb.utils.contains("MACHINE_FEATURES", "efi", "${EFI_PROVIDER}", "", d)}"

# Include legacy boot if MACHINE_FEATURES includes "pcbios" or if it does not
# contain "efi". This way legacy is supported by default if neither is
# specified, maintaining the original behavior.
def pcbios(d):
    pcbios = bb.utils.contains("MACHINE_FEATURES", "pcbios", "1", "0", d)
    if pcbios == "0":
        pcbios = bb.utils.contains("MACHINE_FEATURES", "efi", "0", "1", d)
    return pcbios

def pcbios_class(d):
    if d.getVar("PCBIOS", True) == "1":
        return "syslinux"
    return ""

PCBIOS = "${@pcbios(d)}"
PCBIOS_CLASS = "${@pcbios_class(d)}"

inherit ${PCBIOS_CLASS}
inherit ${EFI_CLASS}

# Get the build_syslinux_cfg() function from the syslinux class

AUTO_SYSLINUXCFG = "1"
DISK_SIGNATURE ?= "${DISK_SIGNATURE_GENERATED}"
SYSLINUX_ROOT ?= "root=/dev/sda2"
SYSLINUX_TIMEOUT ?= "10"

IS_VM = '${@bb.utils.contains_any("IMAGE_FSTYPES", ["vmdk", "vdi", "qcow2"], "true", "false", d)}'

boot_direct_populate() {
	dest=$1
	install -d $dest

	# Install bzImage, initrd, and rootfs.img in DEST for all loaders to use.
	if [ -e ${DEPLOY_DIR_IMAGE}/bzImage ]; then
		install -m 0644 ${DEPLOY_DIR_IMAGE}/bzImage $dest/vmlinuz
	fi

	# initrd is made of concatenation of multiple filesystem images
	if [ -n "${INITRD}" ]; then
		rm -f $dest/initrd
		for fs in ${INITRD}
		do
			if [ -s "${fs}" ]; then
				cat ${fs} >> $dest/initrd
			else
				bbfatal "${fs} is invalid. initrd image creation failed."
			fi
		done
		chmod 0644 $dest/initrd
	fi
}

build_boot_dd() {
	HDDDIR="${S}/hdd/boot"
	HDDIMG="${S}/hdd.image"
	IMAGE=${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.hdddirect

	boot_direct_populate $HDDDIR

	if [ "${PCBIOS}" = "1" ]; then
		syslinux_hddimg_populate $HDDDIR
	fi
	if [ "${EFI}" = "1" ]; then
		efi_hddimg_populate $HDDDIR
	fi

	if [ "${IS_VM}" = "true" ]; then
		if [ "x${AUTO_SYSLINUXMENU}" = "x1" ] ; then
			install -m 0644 ${STAGING_DIR}/${MACHINE}/usr/share/syslinux/vesamenu.c32 $HDDDIR/${SYSLINUXDIR}/
			if [ "x${SYSLINUX_SPLASH}" != "x" ] ; then
				install -m 0644 ${SYSLINUX_SPLASH} $HDDDIR/${SYSLINUXDIR}/splash.lss
			fi
		fi
	fi

	BLOCKS=`du -bks $HDDDIR | cut -f 1`
	BLOCKS=`expr $BLOCKS + ${BOOTDD_EXTRA_SPACE}`

	# Ensure total sectors is an integral number of sectors per
	# track or mcopy will complain. Sectors are 512 bytes, and we
	# generate images with 32 sectors per track. This calculation is
	# done in blocks, thus the mod by 16 instead of 32.
	BLOCKS=$(expr $BLOCKS + $(expr 16 - $(expr $BLOCKS % 16)))

	mkdosfs -n ${BOOTDD_VOLUME_ID} -S 512 -C $HDDIMG $BLOCKS 
	mcopy -i $HDDIMG -s $HDDDIR/* ::/

	if [ "${PCBIOS}" = "1" ]; then
		syslinux_hdddirect_install $HDDIMG
	fi	
	chmod 644 $HDDIMG

	ROOTFSBLOCKS=`du -Lbks ${ROOTFS} | cut -f 1`
	TOTALSIZE=`expr $BLOCKS + $ROOTFSBLOCKS`
	END1=`expr $BLOCKS \* 1024`
	END2=`expr $END1 + 512`
	END3=`expr \( $ROOTFSBLOCKS \* 1024 \) + $END1`

	echo $ROOTFSBLOCKS $TOTALSIZE $END1 $END2 $END3
	rm -rf $IMAGE
	dd if=/dev/zero of=$IMAGE bs=1024 seek=$TOTALSIZE count=1

	parted $IMAGE mklabel msdos
	parted $IMAGE mkpart primary fat16 0 ${END1}B
	parted $IMAGE unit B mkpart primary ext2 ${END2}B ${END3}B
	parted $IMAGE set 1 boot on 

	parted $IMAGE print

	awk "BEGIN { printf \"$(echo ${DISK_SIGNATURE} | fold -w 2 | tac | paste -sd '' | sed 's/\(..\)/\\x&/g')\" }" | \
		dd of=$IMAGE bs=1 seek=440 conv=notrunc

	OFFSET=`expr $END2 / 512`
	if [ "${PCBIOS}" = "1" ]; then
		dd if=${STAGING_DATADIR}/syslinux/mbr.bin of=$IMAGE conv=notrunc
	fi

	dd if=$HDDIMG of=$IMAGE conv=notrunc seek=1 bs=512
	dd if=${ROOTFS} of=$IMAGE conv=notrunc seek=$OFFSET bs=512

	cd ${DEPLOY_DIR_IMAGE}
	rm -f ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.hdddirect
	ln -s ${IMAGE_NAME}.hdddirect ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.hdddirect
} 

python do_bootdirectdisk() {
    validate_disk_signature(d)
    if d.getVar("PCBIOS", True) == "1":
        bb.build.exec_func('build_syslinux_cfg', d)
    if d.getVar("EFI", True) == "1":
        bb.build.exec_func('build_efi_cfg', d)
    bb.build.exec_func('build_boot_dd', d)
}

def generate_disk_signature():
    import uuid

    signature = str(uuid.uuid4())[:8]

    if signature != '00000000':
        return signature
    else:
        return 'ffffffff'

def validate_disk_signature(d):
    import re

    disk_signature = d.getVar("DISK_SIGNATURE", True)

    if not re.match(r'^[0-9a-fA-F]{8}$', disk_signature):
        bb.fatal("DISK_SIGNATURE '%s' must be an 8 digit hex string" % disk_signature)

DISK_SIGNATURE_GENERATED := "${@generate_disk_signature()}"

addtask bootdirectdisk before do_build
