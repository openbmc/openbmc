# image-vm.bbclass
# (loosly based off image-live.bbclass Copyright (C) 2004, Advanced Micro Devices, Inc.)
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

inherit live-vm-common

do_bootdirectdisk[depends] += "dosfstools-native:do_populate_sysroot \
                               virtual/kernel:do_deploy \
                               syslinux:do_populate_sysroot \
                               syslinux-native:do_populate_sysroot \
                               parted-native:do_populate_sysroot \
                               mtools-native:do_populate_sysroot \
                               ${PN}:do_image_${VM_ROOTFS_TYPE} \
                               "

IMAGE_TYPEDEP_vmdk = "${VM_ROOTFS_TYPE}"
IMAGE_TYPEDEP_vdi = "${VM_ROOTFS_TYPE}"
IMAGE_TYPEDEP_qcow2 = "${VM_ROOTFS_TYPE}"
IMAGE_TYPEDEP_hdddirect = "${VM_ROOTFS_TYPE}"
IMAGE_TYPES_MASKED += "vmdk vdi qcow2 hdddirect"

VM_ROOTFS_TYPE ?= "ext4"
ROOTFS ?= "${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${VM_ROOTFS_TYPE}"

# Used by bootloader
LABELS_VM ?= "boot"
ROOT_VM ?= "root=/dev/sda2"
# Using an initramfs is optional. Enable it by setting INITRD_IMAGE_VM.
INITRD_IMAGE_VM ?= ""
INITRD_VM ?= "${@'${IMGDEPLOYDIR}/${INITRD_IMAGE_VM}-${MACHINE}.cpio.gz' if '${INITRD_IMAGE_VM}' else ''}"
do_bootdirectdisk[depends] += "${@'${INITRD_IMAGE_VM}:do_image_complete' if '${INITRD_IMAGE_VM}' else ''}"

BOOTDD_VOLUME_ID   ?= "boot"
BOOTDD_EXTRA_SPACE ?= "16384"

DISK_SIGNATURE ?= "${DISK_SIGNATURE_GENERATED}"
DISK_SIGNATURE[vardepsexclude] = "DISK_SIGNATURE_GENERATED"

build_boot_dd() {
	HDDDIR="${S}/hdd/boot"
	HDDIMG="${S}/hdd.image"
	IMAGE=${IMGDEPLOYDIR}/${IMAGE_NAME}.hdddirect

	populate_kernel $HDDDIR

	if [ "${PCBIOS}" = "1" ]; then
		syslinux_hddimg_populate $HDDDIR
	fi
	if [ "${EFI}" = "1" ]; then
		efi_hddimg_populate $HDDDIR
	fi

	BLOCKS=`du -bks $HDDDIR | cut -f 1`
	BLOCKS=`expr $BLOCKS + ${BOOTDD_EXTRA_SPACE}`

	# Remove it since mkdosfs would fail when it exists
	rm -f $HDDIMG
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

	cd ${IMGDEPLOYDIR}

	ln -sf ${IMAGE_NAME}.hdddirect ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.hdddirect
} 

python do_bootdirectdisk() {
    validate_disk_signature(d)
    set_live_vm_vars(d, 'VM')
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

run_qemu_img (){
    type="$1"
    qemu-img convert -O $type ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.hdddirect ${IMGDEPLOYDIR}/${IMAGE_NAME}.$type

    ln -sf ${IMAGE_NAME}.$type ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.$type
}
create_vmdk_image () {
    run_qemu_img vmdk
}

create_vdi_image () {
    run_qemu_img vdi
}

create_qcow2_image () {
    run_qemu_img qcow2
}

python do_vmimg() {
    if 'vmdk' in d.getVar('IMAGE_FSTYPES', True):
        bb.build.exec_func('create_vmdk_image', d)
    if 'vdi' in d.getVar('IMAGE_FSTYPES', True):
        bb.build.exec_func('create_vdi_image', d)
    if 'qcow2' in d.getVar('IMAGE_FSTYPES', True):
        bb.build.exec_func('create_qcow2_image', d)
}

addtask bootdirectdisk before do_vmimg
addtask vmimg after do_bootdirectdisk before do_image_complete
do_vmimg[depends] += "qemu-native:do_populate_sysroot"
