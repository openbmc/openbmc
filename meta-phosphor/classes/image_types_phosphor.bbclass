# Base image class extension, inlined into every image.

def build_uboot(d):
    fstypes = d.getVar('IMAGE_FSTYPES', True).split()
    if any([x.endswith('u-boot') for x in fstypes]):
        return 'image_types_uboot'
    return ''


# Inherit u-boot classes if legacy uboot images are in use.
IMAGE_TYPE_uboot = '${@build_uboot(d)}'
inherit ${IMAGE_TYPE_uboot}

# Phosphor image types
#
# Phosphor OpenBMC supports a fixed partition mtd layout,
# A dynamic mtd with ubi layout, and a tar file for use with
# The reference BMC software update implementation.

# Image composition
FLASH_KERNEL_IMAGE ?= "${@bb.utils.contains('MACHINE_FEATURES', \
        'obmc-ubi-fs', 'fitImage-${MACHINE}.bin', \
        'fitImage-${INITRAMFS_IMAGE}-${MACHINE}.bin', d)}"
IMAGE_BASETYPE ?= "squashfs-xz"
OVERLAY_BASETYPE ?= "jffs2"
FLASH_UBI_BASETYPE ?= "${IMAGE_BASETYPE}"
FLASH_UBI_OVERLAY_BASETYPE ?= "ubifs"

IMAGE_TYPES += "overlay mtd-static mtd-static-alltar mtd-static-tar mtd-ubi mtd-ubi-tar"

IMAGE_TYPEDEP_overlay = "${IMAGE_BASETYPE}"
IMAGE_TYPEDEP_mtd-static = "${IMAGE_BASETYPE}"
IMAGE_TYPEDEP_mtd-static-tar = "${IMAGE_BASETYPE}"
IMAGE_TYPEDEP_mtd-static-alltar = "mtd-static"
IMAGE_TYPEDEP_mtd-ubi = "${FLASH_UBI_BASETYPE}"
IMAGE_TYPEDEP_mtd-ubi-tar = "${FLASH_UBI_BASETYPE}"
IMAGE_TYPES_MASKED += "overlay mtd-static mtd-static-alltar mtd-static-tar mtd-ubi mtd-ubi-tar"

# Flash characteristics in KB unless otherwise noted
FLASH_SIZE ?= "32768"
FLASH_PEB_SIZE ?= "64"
# Flash page and overhead sizes in bytes
FLASH_PAGE_SIZE ?= "1"
FLASH_NOR_UBI_OVERHEAD ?= "64"

# Fixed partition offsets
FLASH_UBOOT_OFFSET ?= "0"
FLASH_KERNEL_OFFSET ?= "512"
FLASH_UBI_OFFSET ?= "${FLASH_KERNEL_OFFSET}"
FLASH_ROFS_OFFSET ?= "4864"
FLASH_RWFS_OFFSET ?= "28672"

# UBI volume sizes in KB unless otherwise noted.
FLASH_UBI_RWFS_SIZE ?= "4096"
FLASH_UBI_RWFS_TXT_SIZE ?= "4MiB"

python() {
    # Compute rwfs LEB count and LEB size.
    page_size = d.getVar('FLASH_PAGE_SIZE', True)
    nor_overhead_size = d.getVar('FLASH_NOR_UBI_OVERHEAD', True)
    overhead_size = max(int(page_size), int(nor_overhead_size))
    peb_size = d.getVar('FLASH_PEB_SIZE', True)
    leb_size = (int(peb_size) * 1024) - (2 * overhead_size)
    d.setVar('FLASH_LEB_SIZE', str(leb_size)) # In bytes

    rwfs_size = d.getVar('FLASH_UBI_RWFS_SIZE', True)
    rwfs_size = int(rwfs_size) * 1024
    lebs = int((rwfs_size + leb_size - 1) / leb_size) # Rounding up
    d.setVar('FLASH_UBI_RWFS_LEBS', str(lebs))
}

# Allow rwfs mkfs configuration through OVERLAY_MKFS_OPTS and OVERRIDES. However,
# avoid setting 'ext4' or 'jffs2' in OVERRIDES as such raw filesystem types are
# reserved for the primary image (and setting them currently breaks the build).
# Instead, prefix the overlay override value with 'rwfs-' to avoid collisions.
DISTROOVERRIDES .= ":static-rwfs-${OVERLAY_BASETYPE}"
DISTROOVERRIDES .= ":ubi-rwfs-${FLASH_UBI_OVERLAY_BASETYPE}"

JFFS2_RWFS_CMD = "mkfs.jffs2 --root=jffs2 --faketime --output=rwfs.jffs2"
UBIFS_RWFS_CMD = "mkfs.ubifs -r ubifs -c ${FLASH_UBI_RWFS_LEBS} -m ${FLASH_PAGE_SIZE} -e ${FLASH_LEB_SIZE} rwfs.ubifs"

FLASH_STATIC_RWFS_CMD_static-rwfs-jffs2 = "${JFFS2_RWFS_CMD}"
FLASH_UBI_RWFS_CMD_ubi-rwfs-jffs2 = "${JFFS2_RWFS_CMD}"
FLASH_UBI_RWFS_CMD_ubi-rwfs-ubifs = "${UBIFS_RWFS_CMD}"

mk_nor_image() {
	image_dst="$1"
	image_size_kb=$2
	dd if=/dev/zero bs=1k count=$image_size_kb \
		| tr '\000' '\377' > $image_dst
}

make_rwfs() {
	type=$1
	cmd=$2
	shift
	shift
	opts="$@"

	rm -f rwfs.$type
	rm -rf $type
	mkdir $type

	$cmd $opts
}

do_generate_rwfs_static() {
	make_rwfs ${OVERLAY_BASETYPE} "${FLASH_STATIC_RWFS_CMD}" ${OVERLAY_MKFS_OPTS}
}
do_generate_rwfs_static[dirs] = " ${S}/static"
do_generate_rwfs_static[depends] += " \
        mtd-utils-native:do_populate_sysroot \
        "

do_generate_rwfs_ubi() {
	make_rwfs ${FLASH_UBI_OVERLAY_BASETYPE} "${FLASH_UBI_RWFS_CMD}"
}
do_generate_rwfs_ubi[dirs] = " ${S}/ubi"
do_generate_rwfs_ubi[depends] += " \
        mtd-utils-native:do_populate_sysroot \
        "

add_volume() {
	config_file=$1
	vol_id=$2
	vol_type=$3
	vol_name=$4
	image=$5
	vol_size=$6

	echo \[$vol_name\] >> $config_file
	echo mode=ubi >> $config_file
	echo image=$image >> $config_file
	echo vol_type=$vol_type >> $config_file
	echo vol_name=$vol_name >> $config_file
	echo vol_id=$vol_id >> $config_file
	if [ ! -z $vol_size ]; then
		echo vol_size=$vol_size >> $config_file
	fi
}

do_generate_ubi() {
	cfg=ubinize-${IMAGE_NAME}.cfg

	rm -f $cfg ubi-img

	# Construct the ubinize config file
	add_volume $cfg 0 static kernel0 \
		${DEPLOY_DIR_IMAGE}/${FLASH_KERNEL_IMAGE}

	add_volume $cfg 1 static rofs0 \
		${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${FLASH_UBI_BASETYPE}

	add_volume $cfg 2 dynamic rwfs rwfs.${FLASH_UBI_OVERLAY_BASETYPE} ${FLASH_UBI_RWFS_TXT_SIZE}

	# Build the ubi partition image
	ubinize -p ${FLASH_PEB_SIZE}KiB -m ${FLASH_PAGE_SIZE} -o ubi-img $cfg

	# Concatenate the uboot and ubi partitions
	mk_nor_image ${IMGDEPLOYDIR}/${IMAGE_NAME}.ubi.mtd ${FLASH_SIZE}
	dd bs=1k conv=notrunc seek=${FLASH_UBOOT_OFFSET} \
		if=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} \
		of=${IMGDEPLOYDIR}/${IMAGE_NAME}.ubi.mtd
	dd bs=1k conv=notrunc seek=${FLASH_UBI_OFFSET} \
		if=ubi-img \
		of=${IMGDEPLOYDIR}/${IMAGE_NAME}.ubi.mtd

	cd ${IMGDEPLOYDIR}
	ln -sf ${IMAGE_NAME}.ubi.mtd ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.ubi.mtd
}
do_generate_ubi[dirs] = "${S}/ubi"
do_generate_ubi[depends] += " \
        ${PN}:do_image_${@d.getVar('FLASH_UBI_BASETYPE', True).replace('-', '_')} \
        virtual/kernel:do_deploy \
        u-boot:do_populate_sysroot \
        mtd-utils-native:do_populate_sysroot \
        "

do_generate_static() {
	# Assemble the flash image
	mk_nor_image ${IMGDEPLOYDIR}/${IMAGE_NAME}.static.mtd ${FLASH_SIZE}
	dd bs=1k conv=notrunc seek=${FLASH_UBOOT_OFFSET} \
		if=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} \
		of=${IMGDEPLOYDIR}/${IMAGE_NAME}.static.mtd

	dd bs=1k conv=notrunc seek=${FLASH_KERNEL_OFFSET} \
		if=${DEPLOY_DIR_IMAGE}/${FLASH_KERNEL_IMAGE} \
		of=${IMGDEPLOYDIR}/${IMAGE_NAME}.static.mtd

	dd bs=1k conv=notrunc seek=${FLASH_ROFS_OFFSET} \
		if=${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${IMAGE_BASETYPE} \
		of=${IMGDEPLOYDIR}/${IMAGE_NAME}.static.mtd

	dd bs=1k conv=notrunc seek=${FLASH_RWFS_OFFSET} \
		if=rwfs.${OVERLAY_BASETYPE} \
		of=${IMGDEPLOYDIR}/${IMAGE_NAME}.static.mtd
	# File needed for generating non-standard legacy links below
	cp rwfs.${OVERLAY_BASETYPE} ${IMGDEPLOYDIR}/rwfs.${OVERLAY_BASETYPE}

	cd ${IMGDEPLOYDIR}
	ln -sf ${IMAGE_NAME}.static.mtd ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.static.mtd

	# Maintain non-standard legacy links
	ln -sf ${IMAGE_NAME}.static.mtd ${IMGDEPLOYDIR}/flash-${MACHINE}
	ln -sf ${IMAGE_NAME}.static.mtd ${IMGDEPLOYDIR}/image-bmc
	ln -sf u-boot.${UBOOT_SUFFIX} ${IMGDEPLOYDIR}/image-u-boot
	ln -sf ${FLASH_KERNEL_IMAGE} ${IMGDEPLOYDIR}/image-kernel
	ln -sf ${IMAGE_LINK_NAME}.${IMAGE_BASETYPE} ${IMGDEPLOYDIR}/image-rofs
	ln -sf rwfs.${OVERLAY_BASETYPE} ${IMGDEPLOYDIR}/image-rwfs
}
do_generate_static[dirs] = "${S}/static"
do_generate_static[depends] += " \
        ${PN}:do_image_${@d.getVar('IMAGE_BASETYPE', True).replace('-', '_')} \
        virtual/kernel:do_deploy \
        u-boot:do_populate_sysroot \
        "

do_generate_static_alltar() {
	ln -sf ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.static.mtd image-bmc
	tar -h -cvf ${IMGDEPLOYDIR}/${IMAGE_NAME}.static.mtd.all.tar image-bmc

	cd ${IMGDEPLOYDIR}

	ln -sf ${IMAGE_NAME}.static.mtd.all.tar \
		${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.static.mtd.all.tar

	# Maintain non-standard legacy link.
	ln -sf ${IMAGE_NAME}.static.mtd.all.tar \
		${IMGDEPLOYDIR}/${MACHINE}-${DATETIME}.all.tar
}
do_generate_static_alltar[vardepsexclude] = "DATETIME"
do_generate_static_alltar[dirs] = "${S}/static"

make_tar_of_images() {
	rwfs=$1
	rofs=$2
	type=$3
	shift
	shift
	shift
	extra_files="$@"

	# Create some links to help make the tar archive
	ln -sf ${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} image-u-boot
	ln -sf ${DEPLOY_DIR_IMAGE}/${FLASH_KERNEL_IMAGE} image-kernel
	ln -sf ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.$rofs image-rofs
	ln -sf rwfs.$rwfs image-rwfs

	# Create the tar archive
	tar -h -cvf ${IMGDEPLOYDIR}/${IMAGE_NAME}.$type.mtd.tar \
		image-u-boot image-kernel image-rofs image-rwfs $extra_files

	cd ${IMGDEPLOYDIR}
	ln -sf ${IMAGE_NAME}.$type.mtd.tar ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.$type.mtd.tar
}

do_generate_static_tar() {
	make_tar_of_images ${OVERLAY_BASETYPE} ${IMAGE_BASETYPE} static

	# Maintain non-standard legacy link.
	cd ${IMGDEPLOYDIR}
	ln -sf ${IMAGE_NAME}.static.mtd.tar ${IMGDEPLOYDIR}/${MACHINE}-${DATETIME}.tar
}
do_generate_static_tar[dirs] = " ${S}/static"
do_generate_static_tar[depends] += " \
        ${PN}:do_image_${@d.getVar('IMAGE_BASETYPE', True).replace('-', '_')} \
        virtual/kernel:do_deploy \
        u-boot:do_populate_sysroot \
        "
do_generate_static_tar[vardepsexclude] = "DATETIME"

do_generate_ubi_tar() {
	ln -sf ${S}/MANIFEST MANIFEST
	make_tar_of_images ${FLASH_UBI_OVERLAY_BASETYPE} ${FLASH_UBI_BASETYPE} ubi MANIFEST
}
do_generate_ubi_tar[dirs] = " ${S}/ubi"
do_generate_ubi_tar[depends] += " \
        ${PN}:do_image_${@d.getVar('FLASH_UBI_BASETYPE', True).replace('-', '_')} \
        virtual/kernel:do_deploy \
        u-boot:do_populate_sysroot \
        "

python do_generate_phosphor_manifest() {
    import configparser
    import io
    path = d.getVar('STAGING_DIR_HOST', True) + d.getVar('sysconfdir', True)
    path = os.path.join(path, 'os-release')
    parser = configparser.SafeConfigParser(strict=False)
    parser.optionxform = str
    version = ''
    with open(path, 'r') as fd:
        buf = '[root]\n' + fd.read()
        fd = io.StringIO(buf)
        parser.readfp(fd)
        version = parser['root']['VERSION_ID']

    with open('MANIFEST', 'w') as fd:
        fd.write('purpose=xyz.openbmc_project.Software.Version.VersionPurpose.BMC\n')
        fd.write('version={}\n'.format(version.strip('"')))
}
do_generate_phosphor_manifest[dirs] = "${S}"
do_generate_phosphor_manifest[depends] += " \
        os-release:do_populate_sysroot \
        "

addtask generate_phosphor_manifest after do_rootfs
addtask generate_rwfs_static after do_rootfs
addtask generate_rwfs_ubi after do_rootfs

python() {
    types = d.getVar('IMAGE_FSTYPES', True).split()

    if any([x in types for x in ['mtd-static', 'mtd-static-alltar']]):
        bb.build.addtask(
                'do_generate_static',
                'do_image_complete',
                'do_generate_rwfs_static', d)
    if 'mtd-static-alltar' in types:
        bb.build.addtask(
                'do_generate_static_alltar',
                'do_image_complete',
                'do_generate_static', d)
    if 'mtd-static-tar' in types:
        bb.build.addtask(
                'do_generate_static_tar',
                'do_image_complete',
                'do_generate_rwfs_static', d)

    if 'mtd-ubi' in types:
        bb.build.addtask(
                'do_generate_ubi',
                'do_image_complete',
                'do_generate_rwfs_ubi', d)
    if 'mtd-ubi-tar' in types:
        bb.build.addtask(
                'do_generate_ubi_tar',
                'do_image_complete',
                'do_generate_rwfs_ubi do_generate_phosphor_manifest', d)
}
