# IMAGE_NAME is the base name for everything produced when building images.
# The actual image that contains the rootfs has an additional suffix (.rootfs
# by default) followed by additional suffices which describe the format (.ext4,
# .ext4.xz, etc.).
IMAGE_NAME_SUFFIX ??= ".rootfs"

# The default aligment of the size of the rootfs is set to 1KiB. In case
# you're using the SD card emulation of a QEMU system simulator you may
# set this value to 2048 (2MiB alignment).
IMAGE_ROOTFS_ALIGNMENT ?= "1"

def imagetypes_getdepends(d):
    def adddep(depstr, deps):
        for d in (depstr or "").split():
            # Add task dependency if not already present
            if ":" not in d:
                d += ":do_populate_sysroot"
            deps.add(d)

    # Take a type in the form of foo.bar.car and split it into the items
    # needed for the image deps "foo", and the conversion deps ["bar", "car"]
    def split_types(typestring):
        types = typestring.split(".")
        return types[0], types[1:]

    fstypes = set((d.getVar('IMAGE_FSTYPES') or "").split())
    fstypes |= set((d.getVar('IMAGE_FSTYPES_DEBUGFS') or "").split())

    deprecated = set()
    deps = set()
    for typestring in fstypes:
        basetype, resttypes = split_types(typestring)

        var = "IMAGE_DEPENDS_%s" % basetype
        if d.getVar(var) is not None:
            deprecated.add(var)

        for typedepends in (d.getVar("IMAGE_TYPEDEP_%s" % basetype) or "").split():
            base, rest = split_types(typedepends)
            resttypes += rest

            var = "IMAGE_DEPENDS_%s" % base
            if d.getVar(var) is not None:
                deprecated.add(var)

        for ctype in resttypes:
            adddep(d.getVar("CONVERSION_DEPENDS_%s" % ctype), deps)
            adddep(d.getVar("COMPRESS_DEPENDS_%s" % ctype), deps)

    if deprecated:
        bb.fatal('Deprecated variable(s) found: "%s". '
                 'Use do_image_<type>[depends] += "<recipe>:<task>" instead' % ', '.join(deprecated))

    # Sort the set so that ordering is consistant
    return " ".join(sorted(deps))

XZ_COMPRESSION_LEVEL ?= "-9"
XZ_INTEGRITY_CHECK ?= "crc32"

ZIP_COMPRESSION_LEVEL ?= "-9"

ZSTD_COMPRESSION_LEVEL ?= "-3"

JFFS2_SUM_EXTRA_ARGS ?= ""
IMAGE_CMD_jffs2 = "mkfs.jffs2 --root=${IMAGE_ROOTFS} --faketime --output=${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.jffs2 ${EXTRA_IMAGECMD}"

IMAGE_CMD_cramfs = "mkfs.cramfs ${IMAGE_ROOTFS} ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.cramfs ${EXTRA_IMAGECMD}"

oe_mkext234fs () {
	fstype=$1
	extra_imagecmd=""

	if [ $# -gt 1 ]; then
		shift
		extra_imagecmd=$@
	fi

	# If generating an empty image the size of the sparse block should be large
	# enough to allocate an ext4 filesystem using 4096 bytes per inode, this is
	# about 60K, so dd needs a minimum count of 60, with bs=1024 (bytes per IO)
	eval local COUNT=\"0\"
	eval local MIN_COUNT=\"60\"
	if [ $ROOTFS_SIZE -lt $MIN_COUNT ]; then
		eval COUNT=\"$MIN_COUNT\"
	fi
	# Create a sparse image block
	bbdebug 1 Executing "dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype seek=$ROOTFS_SIZE count=$COUNT bs=1024"
	dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype seek=$ROOTFS_SIZE count=$COUNT bs=1024
	bbdebug 1 "Actual Rootfs size:  `du -s ${IMAGE_ROOTFS}`"
	bbdebug 1 "Actual Partion size: `stat -c '%s' ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype`"
	bbdebug 1 Executing "mkfs.$fstype -F $extra_imagecmd ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype -d ${IMAGE_ROOTFS}"
	mkfs.$fstype -F $extra_imagecmd ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype -d ${IMAGE_ROOTFS}
	# Error codes 0-3 indicate successfull operation of fsck (no errors or errors corrected)
	fsck.$fstype -pvfD ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype || [ $? -le 3 ]
}

IMAGE_CMD_ext2 = "oe_mkext234fs ext2 ${EXTRA_IMAGECMD}"
IMAGE_CMD_ext3 = "oe_mkext234fs ext3 ${EXTRA_IMAGECMD}"
IMAGE_CMD_ext4 = "oe_mkext234fs ext4 ${EXTRA_IMAGECMD}"

MIN_BTRFS_SIZE ?= "16384"
IMAGE_CMD_btrfs () {
	size=${ROOTFS_SIZE}
	if [ ${size} -lt ${MIN_BTRFS_SIZE} ] ; then
		size=${MIN_BTRFS_SIZE}
		bbwarn "Rootfs size is too small for BTRFS. Filesystem will be extended to ${size}K"
	fi
	dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.btrfs seek=${size} count=0 bs=1024
	mkfs.btrfs ${EXTRA_IMAGECMD} -r ${IMAGE_ROOTFS} ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.btrfs
}

IMAGE_CMD_squashfs = "mksquashfs ${IMAGE_ROOTFS} ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.squashfs ${EXTRA_IMAGECMD} -noappend"
IMAGE_CMD_squashfs-xz = "mksquashfs ${IMAGE_ROOTFS} ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.squashfs-xz ${EXTRA_IMAGECMD} -noappend -comp xz"
IMAGE_CMD_squashfs-lzo = "mksquashfs ${IMAGE_ROOTFS} ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.squashfs-lzo ${EXTRA_IMAGECMD} -noappend -comp lzo"
IMAGE_CMD_squashfs-lz4 = "mksquashfs ${IMAGE_ROOTFS} ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.squashfs-lz4 ${EXTRA_IMAGECMD} -noappend -comp lz4"

# By default, tar from the host is used, which can be quite old. If
# you need special parameters (like --xattrs) which are only supported
# by GNU tar upstream >= 1.27, then override that default:
# IMAGE_CMD_TAR = "tar --xattrs --xattrs-include=*"
# do_image_tar[depends] += "tar-replacement-native:do_populate_sysroot"
# EXTRANATIVEPATH += "tar-native"
#
# The GNU documentation does not specify whether --xattrs-include is necessary.
# In practice, it turned out to be not needed when creating archives and
# required when extracting, but it seems prudent to use it in both cases.
IMAGE_CMD_TAR ?= "tar"
# ignore return code 1 "file changed as we read it" as other tasks(e.g. do_image_wic) may be hardlinking rootfs
IMAGE_CMD_tar = "${IMAGE_CMD_TAR} --sort=name --numeric-owner -cf ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.tar -C ${IMAGE_ROOTFS} . || [ $? -eq 1 ]"

do_image_cpio[cleandirs] += "${WORKDIR}/cpio_append"
IMAGE_CMD_cpio () {
	(cd ${IMAGE_ROOTFS} && find . | sort | cpio --reproducible -o -H newc >${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.cpio)
	# We only need the /init symlink if we're building the real
	# image. The -dbg image doesn't need it! By being clever
	# about this we also avoid 'touch' below failing, as it
	# might be trying to touch /sbin/init on the host since both
	# the normal and the -dbg image share the same WORKDIR
	if [ "${IMAGE_BUILDING_DEBUGFS}" != "true" ]; then
		if [ ! -L ${IMAGE_ROOTFS}/init ] && [ ! -e ${IMAGE_ROOTFS}/init ]; then
			if [ -L ${IMAGE_ROOTFS}/sbin/init ] || [ -e ${IMAGE_ROOTFS}/sbin/init ]; then
				ln -sf /sbin/init ${WORKDIR}/cpio_append/init
			else
				touch ${WORKDIR}/cpio_append/init
			fi
			(cd  ${WORKDIR}/cpio_append && echo ./init | cpio -oA -H newc -F ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.cpio)
		fi
	fi
}

UBI_VOLNAME ?= "${MACHINE}-rootfs"

multiubi_mkfs() {
	local mkubifs_args="$1"
	local ubinize_args="$2"
    
        # Added prompt error message for ubi and ubifs image creation.
        if [ -z "$mkubifs_args" ] || [ -z "$ubinize_args" ]; then
            bbfatal "MKUBIFS_ARGS and UBINIZE_ARGS have to be set, see http://www.linux-mtd.infradead.org/faq/ubifs.html for details"
        fi
    
	if [ -z "$3" ]; then
		local vname=""
	else
		local vname="_$3"
	fi

	echo \[ubifs\] > ubinize${vname}-${IMAGE_NAME}.cfg
	echo mode=ubi >> ubinize${vname}-${IMAGE_NAME}.cfg
	echo image=${IMGDEPLOYDIR}/${IMAGE_NAME}${vname}${IMAGE_NAME_SUFFIX}.ubifs >> ubinize${vname}-${IMAGE_NAME}.cfg
	echo vol_id=0 >> ubinize${vname}-${IMAGE_NAME}.cfg
	echo vol_type=dynamic >> ubinize${vname}-${IMAGE_NAME}.cfg
	echo vol_name=${UBI_VOLNAME} >> ubinize${vname}-${IMAGE_NAME}.cfg
	echo vol_flags=autoresize >> ubinize${vname}-${IMAGE_NAME}.cfg
	if [ -n "$vname" ]; then
		mkfs.ubifs -r ${IMAGE_ROOTFS} -o ${IMGDEPLOYDIR}/${IMAGE_NAME}${vname}${IMAGE_NAME_SUFFIX}.ubifs ${mkubifs_args}
	fi
	ubinize -o ${IMGDEPLOYDIR}/${IMAGE_NAME}${vname}${IMAGE_NAME_SUFFIX}.ubi ${ubinize_args} ubinize${vname}-${IMAGE_NAME}.cfg

	# Cleanup cfg file
	mv ubinize${vname}-${IMAGE_NAME}.cfg ${IMGDEPLOYDIR}/

	# Create own symlinks for 'named' volumes
	if [ -n "$vname" ]; then
		cd ${IMGDEPLOYDIR}
		if [ -e ${IMAGE_NAME}${vname}${IMAGE_NAME_SUFFIX}.ubifs ]; then
			ln -sf ${IMAGE_NAME}${vname}${IMAGE_NAME_SUFFIX}.ubifs \
			${IMAGE_LINK_NAME}${vname}.ubifs
		fi
		if [ -e ${IMAGE_NAME}${vname}${IMAGE_NAME_SUFFIX}.ubi ]; then
			ln -sf ${IMAGE_NAME}${vname}${IMAGE_NAME_SUFFIX}.ubi \
			${IMAGE_LINK_NAME}${vname}.ubi
		fi
		cd -
	fi
}

IMAGE_CMD_multiubi () {
	# Split MKUBIFS_ARGS_<name> and UBINIZE_ARGS_<name>
	for name in ${MULTIUBI_BUILD}; do
		eval local mkubifs_args=\"\$MKUBIFS_ARGS_${name}\"
		eval local ubinize_args=\"\$UBINIZE_ARGS_${name}\"

		multiubi_mkfs "${mkubifs_args}" "${ubinize_args}" "${name}"
	done
}

IMAGE_CMD_ubi () {
	multiubi_mkfs "${MKUBIFS_ARGS}" "${UBINIZE_ARGS}"
}
IMAGE_TYPEDEP_ubi = "ubifs"

IMAGE_CMD_ubifs = "mkfs.ubifs -r ${IMAGE_ROOTFS} -o ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.ubifs ${MKUBIFS_ARGS}"

MIN_F2FS_SIZE ?= "524288"
IMAGE_CMD_f2fs () {
        # We need to add additional smarts here form devices smaller than 1.5G
        # Need to scale appropriately between 40M -> 1.5G as the "overprovision
        # ratio" goes down as the device gets bigger (70% -> 4.5%), below about
        # 500M the standard IMAGE_OVERHEAD_FACTOR does not work, so add additional
        # space here when under 500M
	size=${ROOTFS_SIZE}
	if [ ${size} -lt ${MIN_F2FS_SIZE} ] ; then
		size=${MIN_F2FS_SIZE}
		bbwarn "Rootfs size is too small for F2FS. Filesystem will be extended to ${size}K"
	fi
	dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.f2fs seek=${size} count=0 bs=1024
	mkfs.f2fs ${EXTRA_IMAGECMD} ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.f2fs
	sload.f2fs -f ${IMAGE_ROOTFS} ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.f2fs
}

EXTRA_IMAGECMD = ""

inherit siteinfo kernel-arch
JFFS2_ENDIANNESS ?= "${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'le', '-l', '-b', d)}"
JFFS2_ERASEBLOCK ?= "0x40000"
EXTRA_IMAGECMD_jffs2 ?= "--pad ${JFFS2_ENDIANNESS} --eraseblock=${JFFS2_ERASEBLOCK} --no-cleanmarkers"

# Change these if you want default mkfs behavior (i.e. create minimal inode number)
EXTRA_IMAGECMD_ext2 ?= "-i 4096"
EXTRA_IMAGECMD_ext3 ?= "-i 4096"
EXTRA_IMAGECMD_ext4 ?= "-i 4096"
EXTRA_IMAGECMD_btrfs ?= "-n 4096"
EXTRA_IMAGECMD_f2fs ?= ""

do_image_cpio[depends] += "cpio-native:do_populate_sysroot"
do_image_jffs2[depends] += "mtd-utils-native:do_populate_sysroot"
do_image_cramfs[depends] += "util-linux-native:do_populate_sysroot"
do_image_ext2[depends] += "e2fsprogs-native:do_populate_sysroot"
do_image_ext3[depends] += "e2fsprogs-native:do_populate_sysroot"
do_image_ext4[depends] += "e2fsprogs-native:do_populate_sysroot"
do_image_btrfs[depends] += "btrfs-tools-native:do_populate_sysroot"
do_image_squashfs[depends] += "squashfs-tools-native:do_populate_sysroot"
do_image_squashfs_xz[depends] += "squashfs-tools-native:do_populate_sysroot"
do_image_squashfs_lzo[depends] += "squashfs-tools-native:do_populate_sysroot"
do_image_squashfs_lz4[depends] += "squashfs-tools-native:do_populate_sysroot"
do_image_ubi[depends] += "mtd-utils-native:do_populate_sysroot"
do_image_ubifs[depends] += "mtd-utils-native:do_populate_sysroot"
do_image_multiubi[depends] += "mtd-utils-native:do_populate_sysroot"
do_image_f2fs[depends] += "f2fs-tools-native:do_populate_sysroot"

# This variable is available to request which values are suitable for IMAGE_FSTYPES
IMAGE_TYPES = " \
    jffs2 jffs2.sum \
    cramfs \
    ext2 ext2.gz ext2.bz2 ext2.lzma \
    ext3 ext3.gz \
    ext4 ext4.gz \
    btrfs \
    iso \
    hddimg \
    squashfs squashfs-xz squashfs-lzo squashfs-lz4 \
    ubi ubifs multiubi \
    tar tar.gz tar.bz2 tar.xz tar.lz4 tar.zst \
    cpio cpio.gz cpio.xz cpio.lzma cpio.lz4 \
    wic wic.gz wic.bz2 wic.lzma \
    container \
    f2fs \
"

# Compression is a special case of conversion. The old variable
# names are still supported for backward-compatibility. When defining
# new compression or conversion commands, use CONVERSIONTYPES and
# CONVERSION_CMD/DEPENDS.
COMPRESSIONTYPES ?= ""

CONVERSIONTYPES = "gz bz2 lzma xz lz4 lzo zip zst sum md5sum sha1sum sha224sum sha256sum sha384sum sha512sum bmap u-boot vmdk vdi qcow2 base64 ${COMPRESSIONTYPES}"
CONVERSION_CMD_lzma = "lzma -k -f -7 ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}"
CONVERSION_CMD_gz = "gzip -f -9 -n -c --rsyncable ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.gz"
CONVERSION_CMD_bz2 = "pbzip2 -f -k ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}"
CONVERSION_CMD_xz = "xz -f -k -c ${XZ_COMPRESSION_LEVEL} ${XZ_DEFAULTS} --check=${XZ_INTEGRITY_CHECK} ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.xz"
CONVERSION_CMD_lz4 = "lz4 -9 -z -l ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.lz4"
CONVERSION_CMD_lzo = "lzop -9 ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}"
CONVERSION_CMD_zip = "zip ${ZIP_COMPRESSION_LEVEL} ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.zip ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}"
CONVERSION_CMD_zst = "zstd -f -k -T0 -c ${ZSTD_COMPRESSION_LEVEL} ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.zst"
CONVERSION_CMD_sum = "sumtool -i ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} -o ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sum ${JFFS2_SUM_EXTRA_ARGS}"
CONVERSION_CMD_md5sum = "md5sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.md5sum"
CONVERSION_CMD_sha1sum = "sha1sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sha1sum"
CONVERSION_CMD_sha224sum = "sha224sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sha224sum"
CONVERSION_CMD_sha256sum = "sha256sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sha256sum"
CONVERSION_CMD_sha384sum = "sha384sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sha384sum"
CONVERSION_CMD_sha512sum = "sha512sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sha512sum"
CONVERSION_CMD_bmap = "bmaptool create ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} -o ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.bmap"
CONVERSION_CMD_u-boot = "mkimage -A ${UBOOT_ARCH} -O linux -T ramdisk -C none -n ${IMAGE_NAME} -d ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.u-boot"
CONVERSION_CMD_vmdk = "qemu-img convert -O vmdk ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.vmdk"
CONVERSION_CMD_vdi = "qemu-img convert -O vdi ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.vdi"
CONVERSION_CMD_qcow2 = "qemu-img convert -O qcow2 ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.qcow2"
CONVERSION_CMD_base64 = "base64 ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.base64"
CONVERSION_DEPENDS_lzma = "xz-native"
CONVERSION_DEPENDS_gz = "pigz-native"
CONVERSION_DEPENDS_bz2 = "pbzip2-native"
CONVERSION_DEPENDS_xz = "xz-native"
CONVERSION_DEPENDS_lz4 = "lz4-native"
CONVERSION_DEPENDS_lzo = "lzop-native"
CONVERSION_DEPENDS_zip = "zip-native"
CONVERSION_DEPENDS_zst = "zstd-native"
CONVERSION_DEPENDS_sum = "mtd-utils-native"
CONVERSION_DEPENDS_bmap = "bmap-tools-native"
CONVERSION_DEPENDS_u-boot = "u-boot-tools-native"
CONVERSION_DEPENDS_vmdk = "qemu-system-native"
CONVERSION_DEPENDS_vdi = "qemu-system-native"
CONVERSION_DEPENDS_qcow2 = "qemu-system-native"
CONVERSION_DEPENDS_base64 = "coreutils-native"

RUNNABLE_IMAGE_TYPES ?= "ext2 ext3 ext4"
RUNNABLE_MACHINE_PATTERNS ?= "qemu"

DEPLOYABLE_IMAGE_TYPES ?= "hddimg iso" 

# The IMAGE_TYPES_MASKED variable will be used to mask out from the IMAGE_FSTYPES,
# images that will not be built at do_rootfs time: vmdk, vdi, qcow2, hddimg, iso, etc.
IMAGE_TYPES_MASKED ?= ""

# bmap requires python3 to be in the PATH
EXTRANATIVEPATH += "${@'python3-native' if d.getVar('IMAGE_FSTYPES').find('.bmap') else ''}"
