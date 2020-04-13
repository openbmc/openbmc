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

IMAGE_TYPES += "mtd-static mtd-static-alltar mtd-static-tar mtd-ubi mtd-ubi-tar mmc-verity mmc-ext4-tar"

IMAGE_TYPEDEP_mtd-static = "${IMAGE_BASETYPE}"
IMAGE_TYPEDEP_mtd-static-tar = "${IMAGE_BASETYPE}"
IMAGE_TYPEDEP_mtd-static-alltar = "mtd-static"
IMAGE_TYPEDEP_mtd-ubi = "${FLASH_UBI_BASETYPE}"
IMAGE_TYPEDEP_mtd-ubi-tar = "${FLASH_UBI_BASETYPE}"
IMAGE_TYPEDEP_mmc-verity = "${FLASH_EXT4_BASETYPE}"
IMAGE_TYPEDEP_mmc-ext4-tar = "${FLASH_EXT4_BASETYPE}"
IMAGE_TYPES_MASKED += "mtd-static mtd-static-alltar mtd-static-tar mtd-ubi mtd-ubi-tar mmc-verity mmc-ext4-tar"

IMAGE_BLOCK_SIZE ?= "4096"
EXTRA_IMAGECMD_ext4 = "-b ${IMAGE_BLOCK_SIZE}"

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
        u-boot:do_populate_sysroot \
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
        u-boot:do_populate_sysroot \
        mtd-utils-native:do_populate_sysroot \
        "

python do_generate_mmc_verity() {
    import os
    import subprocess

    rootfs_image = os.path.join(d.getVar('IMGDEPLOYDIR', True),
                                '%s.%s' % (
                                    d.getVar('IMAGE_LINK_NAME', True),
                                    d.getVar('FLASH_EXT4_BASETYPE', True)))

    verity_image = 'verity-%s.%s' % (
                            d.getVar('IMAGE_LINK_NAME', True),
                            d.getVar('FLASH_EXT4_BASETYPE', True))

    subprocess.check_call(['dd',
                           'if=%s' % rootfs_image,
                           'of=%s' % verity_image])

    # dm-verity values
    sector_size = 512
    block_size = int(d.getVar('IMAGE_BLOCK_SIZE', True))
    verity_algo = "sha256"
    rootfs_image_size = os.stat(rootfs_image).st_size

    def align_up(number, boundary):
        return ((number + (boundary - 1)) // boundary) * boundary

    # verity metadata must be aligned on the block boundary subsequent to the
    # end of the data
    verity_hash_offset = align_up(rootfs_image_size, block_size)

    verity_hash_blocks = verity_hash_offset // block_size

    # the output of 'veritysetup format' looks like:
    # VERITY header information for obmc-phosphor-image-witherspoon.squashfs-xz
    # UUID:                269b5934-de5b-45b0-99a3-56b219a7b544
    # Hash type:           1
    # Data blocks:         4523
    # Data block size:     4096
    # Hash block size:     4096
    # Hash algorithm:      sha256
    # Salt:                8fca9eff342fc0cf2964057257ea80813a223cb2e540a38458142edeb190e12e
    # Root hash:           38ef00d23fa89300dcf66e7494d25246d03bf846b4119b34e7b1587c0b6fe1d9
    verity_hash_file = "verity-hash-verification-data"
    with open(verity_hash_file, 'w') as f:
       subprocess.check_call(['veritysetup',
                              'format',
                              '--hash=%s'% verity_algo,
                              '--data-block-size=%i' % block_size,
                              '--hash-block-size=%i' % block_size,
                              '--hash-offset=%i' % verity_hash_offset,
                              '%s' % verity_image,
                              '%s' % verity_image], stdout=f, stderr=f)

    for line in open(verity_hash_file, "r"):
        if "Salt" in line:
            verity_salt = line.split()[-1]
        if "Root" in line:
            verity_root = line.split()[-1]

    verity_image_size = os.stat(verity_image).st_size

    # Make an appropriately sized image for MMC
    mmc_image_name = "%s.mmc" % d.getVar('IMAGE_NAME', True)
    mmc_image_path = os.path.join(d.getVar('IMGDEPLOYDIR', True),
                                    '%s' % mmc_image_name)

    # CSD size accommodates MMC limitations that are relevant to QEMU
    csd_size = (1 << (9 + 9 - 1 + 2))

    with open(mmc_image_path, 'w') as fd:
        os.posix_fallocate(fd.fileno(), 0, align_up(verity_image_size, csd_size))
    subprocess.check_call(['dd',
                           'if=%s' % verity_image,
                           'of=%s' % mmc_image_path,
                           'conv=notrunc'])

    os.chdir("%s" % d.getVar('IMGDEPLOYDIR', True))
    mmc_link_name = os.path.join(d.getVar('IMGDEPLOYDIR', True),
                                '%s.mmc' % d.getVar('IMAGE_LINK_NAME', True))
    if os.path.lexists(mmc_link_name):
        os.remove(mmc_link_name)
    os.symlink(mmc_image_name, mmc_link_name)
}
do_generate_mmc_verity[dirs] = "${S}/mmc"
do_generate_mmc_verity[depends] += " \
    ${PN}:do_image_${FLASH_EXT4_BASETYPE} \
    cryptsetup-native:do_populate_sysroot \
    "

do_mk_static_nor_image() {
	# Assemble the flash image
	mk_empty_image ${IMGDEPLOYDIR}/${IMAGE_NAME}.static.mtd ${FLASH_SIZE}
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
	ln -sf ${IMAGE_NAME}.static.mtd ${IMGDEPLOYDIR}/flash-${MACHINE}
	ln -sf ${IMAGE_NAME}.static.mtd ${IMGDEPLOYDIR}/image-bmc
	ln -sf u-boot.${UBOOT_SUFFIX} ${IMGDEPLOYDIR}/image-u-boot
	ln -sf ${FLASH_KERNEL_IMAGE} ${IMGDEPLOYDIR}/image-kernel
	ln -sf ${IMAGE_LINK_NAME}.${IMAGE_BASETYPE} ${IMGDEPLOYDIR}/image-rofs
	ln -sf ${IMAGE_LINK_NAME}.${OVERLAY_BASETYPE} ${IMGDEPLOYDIR}/image-rwfs
}
do_generate_static[dirs] = "${S}/static"
do_generate_static[depends] += " \
        ${PN}:do_image_${@d.getVar('IMAGE_BASETYPE', True).replace('-', '_')} \
        virtual/kernel:do_deploy \
        u-boot:do_populate_sysroot \
        "

make_signatures() {
	signature_files=""
	for file in "$@"; do
		openssl dgst -sha256 -sign ${SIGNING_KEY} -out "${file}.sig" $file
		signature_files="${signature_files} ${file}.sig"
	done
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
	ln -sf ${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} image-u-boot
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
        u-boot:do_populate_sysroot \
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
        u-boot:do_populate_sysroot \
        openssl-native:do_populate_sysroot \
        ${SIGNING_KEY_DEPENDS} \
        ${PN}:do_copy_signing_pubkey \
        "

do_generate_ext4_tar() {
	ln -sf ${S}/MANIFEST MANIFEST
	ln -sf ${S}/publickey publickey
	make_image_links rwfs.${FLASH_EXT4_OVERLAY_BASETYPE} ${FLASH_EXT4_BASETYPE}
	make_signatures image-u-boot image-kernel image-rofs image-rwfs MANIFEST publickey
	make_tar_of_images ext4.mmc MANIFEST publickey ${signature_files}
}
do_generate_ext4_tar[dirs] = " ${S}/ext4"
do_generate_ext4_tar[depends] += " \
        ${PN}:do_image_${FLASH_EXT4_BASETYPE} \
        virtual/kernel:do_deploy \
        u-boot:do_populate_sysroot \
        openssl-native:do_populate_sysroot \
        ${SIGNING_KEY_DEPENDS} \
        ${PN}:do_copy_signing_pubkey \
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
    with open('MANIFEST', 'w') as fd:
        fd.write('purpose={}\n'.format(purpose))
        fd.write('version={}\n'.format(version.strip('"')))
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

    if 'mmc-verity' in types:
        bb.build.addtask(
                'do_generate_mmc_verity',
                'do_image_complete','', d)
    if 'mmc-ext4-tar' in types:
        bb.build.addtask(
                'do_generate_ext4_tar',
                'do_image_complete',
                'do_generate_rwfs_ext4 do_generate_phosphor_manifest', d)
}
