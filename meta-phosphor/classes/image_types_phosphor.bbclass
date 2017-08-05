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
# Phosphor OpenBMC supports a fixed partition mtd layout.

# Image composition
FLASH_KERNEL_IMAGE ?= "fitImage-${INITRAMFS_IMAGE}-${MACHINE}.bin"
IMAGE_BASETYPE ?= "squashfs-xz"
OVERLAY_BASETYPE ?= "jffs2"

IMAGE_TYPES += "mtd-static mtd-static-alltar mtd-static-tar"

IMAGE_TYPEDEP_mtd-static = "${IMAGE_BASETYPE}"
IMAGE_TYPEDEP_mtd-static-tar = "${IMAGE_BASETYPE}"
IMAGE_TYPEDEP_mtd-static-alltar = "mtd-static"
IMAGE_TYPES_MASKED += "mtd-static mtd-static-alltar mtd-static-tar"

# Flash characteristics
FLASH_SIZE ?= "37278"

# Fixed partition offsets
FLASH_UBOOT_OFFSET ?= "0"
FLASH_KERNEL_OFFSET ?= "512"
FLASH_ROFS_OFFSET ?= "4864"
FLASH_RWFS_OFFSET ?= "28672"

# Allow rwfs mkfs configuration through OVERLAY_MKFS_OPTS and OVERRIDES. However,
# avoid setting 'ext4' or 'jffs2' in OVERRIDES as such raw filesystem types are
# reserved for the primary image (and setting them currently breaks the build).
# Instead, prefix the overlay override value with 'rwfs-' to avoid collisions.
DISTROOVERRIDES .= ":static-rwfs-${OVERLAY_BASETYPE}"

JFFS2_RWFS_CMD = "mkfs.jffs2 --root=jffs2 --faketime --output=rwfs.jffs2"

FLASH_STATIC_RWFS_CMD_static-rwfs-jffs2 = "${JFFS2_RWFS_CMD}"

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

do_generate_static() {
	# Assemble the flash image
	truncate -s ${FLASH_SIZE}K ${IMGDEPLOYDIR}/${IMAGE_NAME}.static.mtd
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

	cd ${IMGDEPLOYDIR}
	ln -sf ${IMAGE_NAME}.static.mtd ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.static.mtd

	# Maintain non-standard legacy link
	ln -sf ${IMAGE_NAME}.static.mtd ${IMGDEPLOYDIR}/flash-${MACHINE}
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
}
