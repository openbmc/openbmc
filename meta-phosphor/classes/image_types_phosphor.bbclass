# Base image class extension, inlined into every image.

inherit image_version

# Phosphor image types
#
# Phosphor OpenBMC supports a fixed partition mtd layout,
# A dynamic mtd with ubi layout, and a tar file for use with
# The reference BMC software update implementation.

# Image composition
FLASH_KERNEL_IMAGE ?= "fitImage-${INITRAMFS_IMAGE}-${MACHINE}-${MACHINE}"
FLASH_KERNEL_IMAGE_df-obmc-ubi-fs ?= "fitImage-${MACHINE}.bin"

IMAGE_BASETYPE ?= "squashfs-xz"
OVERLAY_BASETYPE ?= "jffs2"
FLASH_UBI_BASETYPE ?= "${IMAGE_BASETYPE}"
FLASH_UBI_OVERLAY_BASETYPE ?= "ubifs"
FLASH_EXT4_BASETYPE ?= "ext4"
FLASH_EXT4_OVERLAY_BASETYPE ?= "ext4"

IMAGE_TYPES += "mtd-static mtd-static-alltar mtd-static-tar mtd-ubi mtd-ubi-tar mmc-ext4-tar"

IMAGE_TYPEDEP_mtd-static = "${IMAGE_BASETYPE}"
IMAGE_TYPEDEP_mtd-static-tar = "${IMAGE_BASETYPE}"
IMAGE_TYPEDEP_mtd-static-alltar = "mtd-static"
IMAGE_TYPEDEP_mtd-ubi = "${FLASH_UBI_BASETYPE}"
IMAGE_TYPEDEP_mtd-ubi-tar = "${FLASH_UBI_BASETYPE}"
IMAGE_TYPEDEP_mmc-ext4-tar = "${FLASH_EXT4_BASETYPE}"
IMAGE_TYPES_MASKED += "mtd-static mtd-static-alltar mtd-static-tar mtd-ubi mtd-ubi-tar mmc-ext4-tar"

# Flash characteristics in KB unless otherwise noted
DISTROOVERRIDES .= ":flash-${FLASH_SIZE}"
FLASH_SIZE ?= "32768"
FLASH_PEB_SIZE ?= "64"
# Flash page and overhead sizes in bytes
FLASH_PAGE_SIZE ?= "1"
FLASH_NOR_UBI_OVERHEAD ?= "64"

# Fixed partition offsets
FLASH_UBOOT_SPL_SIZE ?= "64"
FLASH_UBOOT_OFFSET ?= "0"
FLASH_KERNEL_OFFSET ?= "512"
FLASH_KERNEL_OFFSET_flash-131072 ?= "1024"
FLASH_UBI_OFFSET ?= "${FLASH_KERNEL_OFFSET}"
FLASH_ROFS_OFFSET ?= "4864"
FLASH_ROFS_OFFSET_flash-131072 ?= "10240"
FLASH_RWFS_OFFSET ?= "28672"
FLASH_RWFS_OFFSET_flash-131072 ?= "98304"

# UBI volume sizes in KB unless otherwise noted.
FLASH_UBI_RWFS_SIZE ?= "6144"
FLASH_UBI_RWFS_SIZE_flash-131072 ?= "32768"
FLASH_UBI_RWFS_TXT_SIZE ?= "6MiB"
FLASH_UBI_RWFS_TXT_SIZE_flash-131072 ?= "32MiB"

# eMMC sizes in KB unless otherwise noted.
MMC_UBOOT_SIZE ?= "1024"
MMC_BOOT_PARTITION_SIZE ?= "65536"

SIGNING_KEY ?= "${STAGING_DIR_NATIVE}${datadir}/OpenBMC.priv"
INSECURE_KEY = "${@'${SIGNING_KEY}' == '${STAGING_DIR_NATIVE}${datadir}/OpenBMC.priv'}"
SIGNING_KEY_DEPENDS = "${@oe.utils.conditional('INSECURE_KEY', 'True', 'phosphor-insecure-signing-key-native:do_populate_sysroot', '', d)}"

VERSION_PURPOSE ?= "xyz.openbmc_project.Software.Version.VersionPurpose.BMC"

UBOOT_SUFFIX ?= "bin"

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
DISTROOVERRIDES .= ":mmc-rwfs-${FLASH_EXT4_OVERLAY_BASETYPE}"

JFFS2_RWFS_CMD = "mkfs.jffs2 --root=jffs2 --faketime --output=${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.jffs2"
UBIFS_RWFS_CMD = "mkfs.ubifs -r ubifs -c ${FLASH_UBI_RWFS_LEBS} -m ${FLASH_PAGE_SIZE} -e ${FLASH_LEB_SIZE} ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.ubifs"
EXT4_RWFS_CMD = "mkfs.ext4 -F ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.rwfs.ext4"

FLASH_STATIC_RWFS_CMD_static-rwfs-jffs2 = "${JFFS2_RWFS_CMD}"
FLASH_UBI_RWFS_CMD_ubi-rwfs-jffs2 = "${JFFS2_RWFS_CMD}"
FLASH_UBI_RWFS_CMD_ubi-rwfs-ubifs = "${UBIFS_RWFS_CMD}"
FLASH_EXT4_RWFS_CMD_mmc-rwfs-ext4 = "${EXT4_RWFS_CMD}"

mk_empty_image() {
	image_dst="$1"
	image_size_kb=$2
	dd if=/dev/zero bs=1k count=$image_size_kb \
		| tr '\000' '\377' > $image_dst
}

mk_empty_image_zeros() {
	image_dst="$1"
	image_size_kb=$2
	dd if=/dev/zero of=$image_dst bs=1k count=$image_size_kb
}

clean_rwfs() {
	type=$1
	shift

	rm -f ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.$type
	rm -rf $type
	mkdir $type
}

make_rwfs() {
	type=$1
	cmd=$2
	shift
	shift
	opts="$@"

	mkdir -p $type

	$cmd $opts
}

do_generate_rwfs_static() {
	clean_rwfs ${OVERLAY_BASETYPE}
	make_rwfs ${OVERLAY_BASETYPE} "${FLASH_STATIC_RWFS_CMD}" ${OVERLAY_MKFS_OPTS}
}
do_generate_rwfs_static[dirs] = " ${S}/static"
do_generate_rwfs_static[depends] += " \
        mtd-utils-native:do_populate_sysroot \
        "

do_generate_rwfs_ubi() {
	clean_rwfs ${FLASH_UBI_OVERLAY_BASETYPE}
	make_rwfs ${FLASH_UBI_OVERLAY_BASETYPE} "${FLASH_UBI_RWFS_CMD}"
}
do_generate_rwfs_ubi[dirs] = " ${S}/ubi"
do_generate_rwfs_ubi[depends] += " \
        mtd-utils-native:do_populate_sysroot \
        "

do_generate_rwfs_ext4() {
	clean_rwfs rwfs.${FLASH_EXT4_OVERLAY_BASETYPE}
	mk_empty_image ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.rwfs.ext4 1024
	make_rwfs ${FLASH_EXT4_OVERLAY_BASETYPE} "${FLASH_EXT4_RWFS_CMD}" ${OVERLAY_MKFS_OPTS}
}
do_generate_rwfs_ext4[dirs] = " ${S}/ext4"
do_generate_rwfs_ext4[depends] += " \
        e2fsprogs-native:do_populate_sysroot \
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

python do_generate_ubi() {
        version_id = do_get_versionID(d)
        d.setVar('VERSION_ID', version_id)
        bb.build.exec_func("do_make_ubi", d)
}
do_generate_ubi[dirs] = "${S}/ubi"
do_generate_ubi[depends] += " \
        ${PN}:do_image_${@d.getVar('FLASH_UBI_BASETYPE', True).replace('-', '_')} \
        virtual/kernel:do_deploy \
        u-boot:do_deploy \
        mtd-utils-native:do_populate_sysroot \
        "

do_make_ubi() {
	cfg=ubinize-${IMAGE_NAME}.cfg
	rm -f $cfg ubi-img
	# Construct the ubinize config file
	add_volume $cfg 0 static kernel-${VERSION_ID} \
		${DEPLOY_DIR_IMAGE}/${FLASH_KERNEL_IMAGE}

	add_volume $cfg 1 static rofs-${VERSION_ID} \
		${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${FLASH_UBI_BASETYPE}

	add_volume $cfg 2 dynamic rwfs ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${FLASH_UBI_OVERLAY_BASETYPE} ${FLASH_UBI_RWFS_TXT_SIZE}

	# Build the ubi partition image
	ubinize -p ${FLASH_PEB_SIZE}KiB -m ${FLASH_PAGE_SIZE} -o ubi-img $cfg

	# Concatenate the uboot and ubi partitions
	mk_empty_image ${IMGDEPLOYDIR}/${IMAGE_NAME}.ubi.mtd ${FLASH_SIZE}
	dd bs=1k conv=notrunc seek=${FLASH_UBOOT_OFFSET} \
		if=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} \
		of=${IMGDEPLOYDIR}/${IMAGE_NAME}.ubi.mtd
	dd bs=1k conv=notrunc seek=${FLASH_UBI_OFFSET} \
		if=ubi-img \
		of=${IMGDEPLOYDIR}/${IMAGE_NAME}.ubi.mtd

	cd ${IMGDEPLOYDIR}
	ln -sf ${IMAGE_NAME}.ubi.mtd ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.ubi.mtd
}
do_make_ubi[dirs] = "${S}/ubi"
do_make_ubi[depends] += " \
        ${PN}:do_image_${@d.getVar('FLASH_UBI_BASETYPE', True).replace('-', '_')} \
        virtual/kernel:do_deploy \
        u-boot:do_deploy \
        mtd-utils-native:do_populate_sysroot \
        "

do_mk_static_nor_image() {
	# Assemble the flash image
	mk_empty_image ${IMGDEPLOYDIR}/${IMAGE_NAME}.static.mtd ${FLASH_SIZE}
}

do_generate_image_uboot_file() {
    image_dst="$1"
    uboot_offset=${FLASH_UBOOT_OFFSET}

    if [ ! -z ${SPL_BINARY} ]; then
        dd bs=1k conv=notrunc seek=${FLASH_UBOOT_OFFSET} \
            if=${DEPLOY_DIR_IMAGE}/u-boot-spl.${UBOOT_SUFFIX} \
            of=${image_dst}
        uboot_offset=${FLASH_UBOOT_SPL_SIZE}
    fi

    dd bs=1k conv=notrunc seek=${uboot_offset} \
        if=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} \
        of=${image_dst}
}

python do_generate_static() {
    import subprocess

    bb.build.exec_func("do_mk_static_nor_image", d)

    nor_image = os.path.join(d.getVar('IMGDEPLOYDIR', True),
                             '%s.static.mtd' % d.getVar('IMAGE_NAME', True))

    def _append_image(imgpath, start_kb, finish_kb):
        imgsize = os.path.getsize(imgpath)
        maxsize = (finish_kb - start_kb) * 1024
        bb.debug(1, 'Considering file size=' + str(imgsize) + ' name=' + imgpath)
        bb.debug(1, 'Spanning start=' + str(start_kb) + 'K end=' + str(finish_kb) + 'K')
        bb.debug(1, 'Compare needed=' + str(imgsize) + ' available=' + str(maxsize) + ' margin=' + str(maxsize - imgsize))
        if imgsize > maxsize:
            bb.fatal("Image '%s' is too large!" % imgpath)

        subprocess.check_call(['dd', 'bs=1k', 'conv=notrunc',
                               'seek=%d' % start_kb,
                               'if=%s' % imgpath,
                               'of=%s' % nor_image])

    uboot_offset = int(d.getVar('FLASH_UBOOT_OFFSET', True))

    spl_binary = d.getVar('SPL_BINARY', True)
    if spl_binary:
        _append_image(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True),
                                   'u-boot-spl.%s' % d.getVar('UBOOT_SUFFIX',True)),
                      int(d.getVar('FLASH_UBOOT_OFFSET', True)),
                      int(d.getVar('FLASH_UBOOT_SPL_SIZE', True)))
        uboot_offset += int(d.getVar('FLASH_UBOOT_SPL_SIZE', True))

    _append_image(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True),
                               'u-boot.%s' % d.getVar('UBOOT_SUFFIX',True)),
                  uboot_offset,
                  int(d.getVar('FLASH_KERNEL_OFFSET', True)))

    _append_image(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True),
                               d.getVar('FLASH_KERNEL_IMAGE', True)),
                  int(d.getVar('FLASH_KERNEL_OFFSET', True)),
                  int(d.getVar('FLASH_ROFS_OFFSET', True)))

    _append_image(os.path.join(d.getVar('IMGDEPLOYDIR', True),
                               '%s.%s' % (
                                    d.getVar('IMAGE_LINK_NAME', True),
                                    d.getVar('IMAGE_BASETYPE', True))),
                  int(d.getVar('FLASH_ROFS_OFFSET', True)),
                  int(d.getVar('FLASH_RWFS_OFFSET', True)))

    _append_image(os.path.join(d.getVar('IMGDEPLOYDIR', True),
                               '%s.%s' % (
                                    d.getVar('IMAGE_LINK_NAME', True),
                                    d.getVar('OVERLAY_BASETYPE', True))),
                  int(d.getVar('FLASH_RWFS_OFFSET', True)),
                  int(d.getVar('FLASH_SIZE', True)))

    bb.build.exec_func("do_mk_static_symlinks", d)
}

do_mk_static_symlinks() {
	cd ${IMGDEPLOYDIR}
	ln -sf ${IMAGE_NAME}.static.mtd ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.static.mtd

	# Maintain non-standard legacy links
	do_generate_image_uboot_file ${IMGDEPLOYDIR}/image-u-boot
	ln -sf ${IMAGE_NAME}.static.mtd ${IMGDEPLOYDIR}/flash-${MACHINE}
	ln -sf ${IMAGE_NAME}.static.mtd ${IMGDEPLOYDIR}/image-bmc
	ln -sf ${FLASH_KERNEL_IMAGE} ${IMGDEPLOYDIR}/image-kernel
	ln -sf ${IMAGE_LINK_NAME}.${IMAGE_BASETYPE} ${IMGDEPLOYDIR}/image-rofs
	ln -sf ${IMAGE_LINK_NAME}.${OVERLAY_BASETYPE} ${IMGDEPLOYDIR}/image-rwfs
}
do_generate_static[dirs] = "${S}/static"
do_generate_static[depends] += " \
        ${PN}:do_image_${@d.getVar('IMAGE_BASETYPE', True).replace('-', '_')} \
        virtual/kernel:do_deploy \
        u-boot:do_deploy \
        "

make_signatures() {
	signature_files=""
	for file in "$@"; do
		openssl dgst -sha256 -sign ${SIGNING_KEY} -out "${file}.sig" $file
		signature_files="${signature_files} ${file}.sig"
	done

	if [ -n "$signature_files" ]; then
		sort_signature_files=`echo "$signature_files" | tr ' ' '\n' | sort | tr '\n' ' '`
		cat $sort_signature_files > image-full
		openssl dgst -sha256 -sign ${SIGNING_KEY} -out image-full.sig image-full
		signature_files="${signature_files} image-full.sig"
		rm -rf image-full
	fi
}

do_generate_static_alltar() {
	ln -sf ${S}/MANIFEST MANIFEST
	ln -sf ${S}/publickey publickey
	ln -sf ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.static.mtd image-bmc

	make_signatures image-bmc MANIFEST publickey

	tar -h -cvf ${IMGDEPLOYDIR}/${IMAGE_NAME}.static.mtd.all.tar \
	    image-bmc MANIFEST publickey ${signature_files}

	cd ${IMGDEPLOYDIR}

	ln -sf ${IMAGE_NAME}.static.mtd.all.tar \
		${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.static.mtd.all.tar

	# Maintain non-standard legacy link.
	ln -sf ${IMAGE_NAME}.static.mtd.all.tar \
		${IMGDEPLOYDIR}/${MACHINE}-${DATETIME}.all.tar

}
do_generate_static_alltar[vardepsexclude] = "DATETIME"
do_generate_static_alltar[dirs] = "${S}/static"
do_generate_static_alltar[depends] += " \
        openssl-native:do_populate_sysroot \
        ${SIGNING_KEY_DEPENDS} \
        ${PN}:do_copy_signing_pubkey \
        "

make_image_links() {
	rwfs=$1
	rofs=$2
	shift
	shift

	# Create some links to help make the tar archive in the format
	# expected by phosphor-bmc-code-mgmt.
	do_generate_image_uboot_file image-u-boot
	ln -sf ${DEPLOY_DIR_IMAGE}/${FLASH_KERNEL_IMAGE} image-kernel
	ln -sf ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.$rofs image-rofs
	ln -sf ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.$rwfs image-rwfs
}

make_tar_of_images() {
	type=$1
	shift
	extra_files="$@"

	# Create the tar archive
	tar -h -cvf ${IMGDEPLOYDIR}/${IMAGE_NAME}.$type.tar \
		image-u-boot image-kernel image-rofs image-rwfs $extra_files

	cd ${IMGDEPLOYDIR}
	ln -sf ${IMAGE_NAME}.$type.tar ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.$type.tar
}

do_generate_static_tar() {
	ln -sf ${S}/MANIFEST MANIFEST
	ln -sf ${S}/publickey publickey
	make_image_links ${OVERLAY_BASETYPE} ${IMAGE_BASETYPE}
	make_signatures image-u-boot image-kernel image-rofs image-rwfs MANIFEST publickey
	make_tar_of_images static.mtd MANIFEST publickey ${signature_files}

	# Maintain non-standard legacy link.
	cd ${IMGDEPLOYDIR}
	ln -sf ${IMAGE_NAME}.static.mtd.tar ${IMGDEPLOYDIR}/${MACHINE}-${DATETIME}.tar
}
do_generate_static_tar[dirs] = " ${S}/static"
do_generate_static_tar[depends] += " \
        ${PN}:do_image_${@d.getVar('IMAGE_BASETYPE', True).replace('-', '_')} \
        virtual/kernel:do_deploy \
        u-boot:do_deploy \
        openssl-native:do_populate_sysroot \
        ${SIGNING_KEY_DEPENDS} \
        ${PN}:do_copy_signing_pubkey \
        "
do_generate_static_tar[vardepsexclude] = "DATETIME"

do_generate_ubi_tar() {
	ln -sf ${S}/MANIFEST MANIFEST
	ln -sf ${S}/publickey publickey
	make_image_links ${FLASH_UBI_OVERLAY_BASETYPE} ${FLASH_UBI_BASETYPE}
	make_signatures image-u-boot image-kernel image-rofs image-rwfs MANIFEST publickey
	make_tar_of_images ubi.mtd MANIFEST publickey ${signature_files}
}
do_generate_ubi_tar[dirs] = " ${S}/ubi"
do_generate_ubi_tar[depends] += " \
        ${PN}:do_image_${@d.getVar('FLASH_UBI_BASETYPE', True).replace('-', '_')} \
        virtual/kernel:do_deploy \
        u-boot:do_deploy \
        openssl-native:do_populate_sysroot \
        ${SIGNING_KEY_DEPENDS} \
        ${PN}:do_copy_signing_pubkey \
        "

do_generate_ext4_tar() {
	# Generate the U-Boot image
	mk_empty_image_zeros image-u-boot ${MMC_UBOOT_SIZE}
	do_generate_image_uboot_file image-u-boot

	# Generate a compressed ext4 filesystem with the fitImage file in it to be
	# flashed to the boot partition of the eMMC
	install -d boot-image
	install -m 644 ${DEPLOY_DIR_IMAGE}/${FLASH_KERNEL_IMAGE} boot-image/fitImage
	mk_empty_image_zeros boot-image.${FLASH_EXT4_BASETYPE} ${MMC_BOOT_PARTITION_SIZE}
	mkfs.ext4 -F -i 4096 -d boot-image boot-image.${FLASH_EXT4_BASETYPE}
	# Error codes 0-3 indicate successfull operation of fsck
	fsck.ext4 -pvfD boot-image.${FLASH_EXT4_BASETYPE} || [ $? -le 3 ]
	zstd -f -k -T0 -c ${ZSTD_COMPRESSION_LEVEL} boot-image.${FLASH_EXT4_BASETYPE} > boot-image.${FLASH_EXT4_BASETYPE}.zst

	# Generate the compressed ext4 rootfs
	zstd -f -k -T0 -c ${ZSTD_COMPRESSION_LEVEL} ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${FLASH_EXT4_BASETYPE} > ${IMAGE_LINK_NAME}.${FLASH_EXT4_BASETYPE}.zst

	ln -sf boot-image.${FLASH_EXT4_BASETYPE}.zst image-kernel
	ln -sf ${IMAGE_LINK_NAME}.${FLASH_EXT4_BASETYPE}.zst image-rofs
	ln -sf ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.rwfs.${FLASH_EXT4_OVERLAY_BASETYPE} image-rwfs
	ln -sf ${S}/MANIFEST MANIFEST
	ln -sf ${S}/publickey publickey

	hostfw_update_file="${DEPLOY_DIR_IMAGE}/hostfw/update/image-hostfw"
	if [ -e "${hostfw_update_file}" ]; then
		ln -sf "${hostfw_update_file}" image-hostfw
		make_signatures image-u-boot image-kernel image-rofs image-rwfs MANIFEST publickey image-hostfw
		make_tar_of_images ext4.mmc MANIFEST publickey ${signature_files} image-hostfw
	else
		make_signatures image-u-boot image-kernel image-rofs image-rwfs MANIFEST publickey
		make_tar_of_images ext4.mmc MANIFEST publickey ${signature_files}
	fi
}
do_generate_ext4_tar[dirs] = " ${S}/ext4"
do_generate_ext4_tar[depends] += " \
        zstd-native:do_populate_sysroot \
        ${PN}:do_image_${FLASH_EXT4_BASETYPE} \
        virtual/kernel:do_deploy \
        u-boot:do_deploy \
        openssl-native:do_populate_sysroot \
        ${SIGNING_KEY_DEPENDS} \
        ${PN}:do_copy_signing_pubkey \
        phosphor-hostfw-image:do_deploy \
        "

def get_pubkey_basedir(d):
    return os.path.join(
        d.getVar('STAGING_DIR_TARGET', True),
        d.getVar('sysconfdir', True).strip(os.sep),
        'activationdata')

def get_pubkey_type(d):
    return os.listdir(get_pubkey_basedir(d))[0]

def get_pubkey_path(d):
    return os.path.join(
        get_pubkey_basedir(d),
        get_pubkey_type(d),
        'publickey')

python do_generate_phosphor_manifest() {
    purpose = d.getVar('VERSION_PURPOSE', True)
    version = do_get_version(d)
    target_machine = d.getVar('MACHINE', True)
    extended_version = (d.getVar('EXTENDED_VERSION', True) or "")
    with open('MANIFEST', 'w') as fd:
        fd.write('purpose={}\n'.format(purpose))
        fd.write('version={}\n'.format(version.strip('"')))
        fd.write('ExtendedVersion={}\n'.format(extended_version))
        fd.write('KeyType={}\n'.format(get_pubkey_type(d)))
        fd.write('HashType=RSA-SHA256\n')
        fd.write('MachineName={}\n'.format(target_machine))
}
do_generate_phosphor_manifest[dirs] = "${S}"
do_generate_phosphor_manifest[depends] += " \
        os-release:do_populate_sysroot \
        phosphor-image-signing:do_populate_sysroot \
        "

python do_copy_signing_pubkey() {
    with open(get_pubkey_path(d), 'r') as read_fd:
        with open('publickey', 'w') as write_fd:
            write_fd.write(read_fd.read())
}

do_copy_signing_pubkey[dirs] = "${S}"
do_copy_signing_pubkey[depends] += " \
        phosphor-image-signing:do_populate_sysroot \
        "

addtask copy_signing_pubkey after do_rootfs
addtask generate_phosphor_manifest after do_rootfs
addtask generate_rwfs_static after do_rootfs
addtask generate_rwfs_ubi after do_rootfs
addtask generate_rwfs_ext4 after do_rootfs

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
                'do_generate_static do_generate_phosphor_manifest', d)
    if 'mtd-static-tar' in types:
        bb.build.addtask(
                'do_generate_static_tar',
                'do_image_complete',
                'do_generate_rwfs_static do_generate_phosphor_manifest', d)

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

    if 'mmc-ext4-tar' in types:
        bb.build.addtask(
                'do_generate_ext4_tar',
                'do_image_complete',
                'do_generate_rwfs_ext4 do_generate_phosphor_manifest', d)
}
