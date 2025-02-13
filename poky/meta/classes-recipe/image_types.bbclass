#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

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

        for typedepends in (d.getVar("IMAGE_TYPEDEP:%s" % basetype) or "").split():
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

XZ_COMPRESSION_LEVEL ?= "-6"
XZ_INTEGRITY_CHECK ?= "crc32"

ZIP_COMPRESSION_LEVEL ?= "-9"

7ZIP_COMPRESSION_LEVEL ?= "9"
7ZIP_COMPRESSION_METHOD ?= "BZip2"
7ZIP_EXTENSION ?= "7z"

JFFS2_SUM_EXTRA_ARGS ?= ""
IMAGE_CMD:jffs2 = "mkfs.jffs2 --root=${IMAGE_ROOTFS} --faketime --output=${IMGDEPLOYDIR}/${IMAGE_NAME}.jffs2 ${EXTRA_IMAGECMD}"

IMAGE_CMD:cramfs = "mkfs.cramfs ${IMAGE_ROOTFS} ${IMGDEPLOYDIR}/${IMAGE_NAME}.cramfs ${EXTRA_IMAGECMD}"

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
	bbdebug 1 Executing "dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}.$fstype seek=$ROOTFS_SIZE count=$COUNT bs=1024"
	dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}.$fstype seek=$ROOTFS_SIZE count=$COUNT bs=1024
	bbdebug 1 "Actual Rootfs size:  `du -s ${IMAGE_ROOTFS}`"
	bbdebug 1 "Actual Partition size: `stat -c '%s' ${IMGDEPLOYDIR}/${IMAGE_NAME}.$fstype`"
	bbdebug 1 Executing "mkfs.$fstype -F $extra_imagecmd ${IMGDEPLOYDIR}/${IMAGE_NAME}.$fstype -d ${IMAGE_ROOTFS}"
	mkfs.$fstype -F $extra_imagecmd ${IMGDEPLOYDIR}/${IMAGE_NAME}.$fstype -d ${IMAGE_ROOTFS}
	# Error codes 0-3 indicate successfull operation of fsck (no errors or errors corrected)
	fsck.$fstype -pvfD ${IMGDEPLOYDIR}/${IMAGE_NAME}.$fstype || [ $? -le 3 ]
}

IMAGE_CMD:ext2 = "oe_mkext234fs ext2 ${EXTRA_IMAGECMD}"
IMAGE_CMD:ext3 = "oe_mkext234fs ext3 ${EXTRA_IMAGECMD}"
IMAGE_CMD:ext4 = "oe_mkext234fs ext4 ${EXTRA_IMAGECMD}"

MIN_BTRFS_SIZE ?= "16384"
IMAGE_CMD:btrfs () {
	size=${ROOTFS_SIZE}
	if [ ${size} -lt ${MIN_BTRFS_SIZE} ] ; then
		size=${MIN_BTRFS_SIZE}
		bbwarn "Rootfs size is too small for BTRFS. Filesystem will be extended to ${size}K"
	fi
	dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}.btrfs seek=${size} count=0 bs=1024
	mkfs.btrfs ${EXTRA_IMAGECMD} -r ${IMAGE_ROOTFS} ${IMGDEPLOYDIR}/${IMAGE_NAME}.btrfs
}

oe_mksquashfs () {
    local comp=$1; shift
    local extra_imagecmd="$@"

    if [ "$comp" = "zstd" ]; then
        suffix="zst"
    fi

    # Use the bitbake reproducible timestamp instead of the hardcoded squashfs one
    export SOURCE_DATE_EPOCH=$(stat -c '%Y' ${IMAGE_ROOTFS})
    mksquashfs ${IMAGE_ROOTFS} ${IMGDEPLOYDIR}/${IMAGE_NAME}.squashfs${comp:+-}${suffix:-$comp} -noappend ${comp:+-comp }$comp $extra_imagecmd
}
IMAGE_CMD:squashfs = "oe_mksquashfs '' ${EXTRA_IMAGECMD}"
IMAGE_CMD:squashfs-xz = "oe_mksquashfs xz ${EXTRA_IMAGECMD}"
IMAGE_CMD:squashfs-lzo = "oe_mksquashfs lzo ${EXTRA_IMAGECMD}"
IMAGE_CMD:squashfs-lz4 = "oe_mksquashfs lz4 ${EXTRA_IMAGECMD}"
IMAGE_CMD:squashfs-zst = "oe_mksquashfs zstd ${EXTRA_IMAGECMD}"

IMAGE_CMD:erofs = "mkfs.erofs ${EXTRA_IMAGECMD} ${IMGDEPLOYDIR}/${IMAGE_NAME}.erofs ${IMAGE_ROOTFS}"
IMAGE_CMD:erofs-lz4 = "mkfs.erofs -zlz4 ${EXTRA_IMAGECMD} ${IMGDEPLOYDIR}/${IMAGE_NAME}.erofs-lz4 ${IMAGE_ROOTFS}"
IMAGE_CMD:erofs-lz4hc = "mkfs.erofs -zlz4hc ${EXTRA_IMAGECMD} ${IMGDEPLOYDIR}/${IMAGE_NAME}.erofs-lz4hc ${IMAGE_ROOTFS}"

# Note that vfat can't handle all types of files that a real linux file system
# can (e.g. device files, symlinks, etc.) and therefore it not suitable for all
# use cases
oe_mkvfatfs () {
    mkfs.vfat $@ -C ${IMGDEPLOYDIR}/${IMAGE_NAME}.vfat ${ROOTFS_SIZE}
    mcopy -i "${IMGDEPLOYDIR}/${IMAGE_NAME}.vfat" -vsmpQ ${IMAGE_ROOTFS}/* ::/
}

IMAGE_CMD:vfat = "oe_mkvfatfs ${EXTRA_IMAGECMD}"

IMAGE_CMD_TAR ?= "tar"
# ignore return code 1 "file changed as we read it" as other tasks(e.g. do_image_wic) may be hardlinking rootfs
IMAGE_CMD:tar = "${IMAGE_CMD_TAR} --sort=name --format=posix --pax-option=delete=atime,delete=ctime --numeric-owner -cf ${IMGDEPLOYDIR}/${IMAGE_NAME}.tar -C ${IMAGE_ROOTFS} . || [ $? -eq 1 ]"
SPDX_IMAGE_PURPOSE:tar = "archive"

do_image_cpio[cleandirs] += "${WORKDIR}/cpio_append"
IMAGE_CMD:cpio () {
	(cd ${IMAGE_ROOTFS} && find . | sort | cpio --reproducible -o -H newc >${IMGDEPLOYDIR}/${IMAGE_NAME}.cpio)
	# We only need the /init symlink if we're building the real
	# image. The -dbg image doesn't need it! By being clever
	# about this we also avoid 'touch' below failing, as it
	# might be trying to touch /sbin/init on the host since both
	# the normal and the -dbg image share the same WORKDIR
	if [ "${IMAGE_BUILDING_DEBUGFS}" != "true" ]; then
		if [ ! -L ${IMAGE_ROOTFS}/init ] && [ ! -e ${IMAGE_ROOTFS}/init ]; then
			if [ -L ${IMAGE_ROOTFS}/sbin/init ] || [ -e ${IMAGE_ROOTFS}/sbin/init ]; then
				ln -sf /sbin/init ${WORKDIR}/cpio_append/init
                                touch -h -r ${IMAGE_ROOTFS}/sbin/init ${WORKDIR}/cpio_append/init
			else
                                touch -r ${IMAGE_ROOTFS} ${WORKDIR}/cpio_append/init
			fi
			(cd  ${WORKDIR}/cpio_append && echo ./init | cpio --reproducible -oA -H newc -F ${IMGDEPLOYDIR}/${IMAGE_NAME}.cpio)
		fi
	fi
}
SPDX_IMAGE_PURPOSE:cpio = "archive"

UBI_VOLNAME ?= "${MACHINE}-rootfs"
UBI_VOLTYPE ?= "dynamic"
UBI_IMGTYPE ?= "ubifs"

write_ubi_config() {
	local vname="$1"

	cat <<EOF > ubinize${vname}-${IMAGE_NAME}.cfg
[ubifs]
mode=ubi
image=${IMGDEPLOYDIR}/${IMAGE_NAME}${vname}.${UBI_IMGTYPE}
vol_id=0
vol_type=${UBI_VOLTYPE}
vol_name=${UBI_VOLNAME}
vol_flags=autoresize
EOF
}

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
	write_ubi_config "${vname}"

	if [ -n "$vname" ]; then
		mkfs.ubifs -r ${IMAGE_ROOTFS} -o ${IMGDEPLOYDIR}/${IMAGE_NAME}${vname}.ubifs ${mkubifs_args}
	fi
	ubinize -o ${IMGDEPLOYDIR}/${IMAGE_NAME}${vname}.ubi ${ubinize_args} ubinize${vname}-${IMAGE_NAME}.cfg

	# Cleanup cfg file
	mv ubinize${vname}-${IMAGE_NAME}.cfg ${IMGDEPLOYDIR}/

	# Create own symlinks for 'named' volumes
	if [ -n "$vname" ]; then
		cd ${IMGDEPLOYDIR}
		if [ -e ${IMAGE_NAME}${vname}.ubifs ]; then
			ln -sf ${IMAGE_NAME}${vname}.ubifs \
			${IMAGE_LINK_NAME}${vname}.ubifs
		fi
		if [ -e ${IMAGE_NAME}${vname}.ubi ]; then
			ln -sf ${IMAGE_NAME}${vname}.ubi \
			${IMAGE_LINK_NAME}${vname}.ubi
		fi
		cd -
	fi
}

MULTIUBI_ARGS = "MKUBIFS_ARGS UBINIZE_ARGS"

IMAGE_CMD:multiubi () {
	${@' '.join(['%s_%s="%s";' % (arg, name, d.getVar('%s_%s' % (arg, name))) for arg in d.getVar('MULTIUBI_ARGS').split() for name in d.getVar('MULTIUBI_BUILD').split()])}
	# Split MKUBIFS_ARGS_<name> and UBINIZE_ARGS_<name>
	for name in ${MULTIUBI_BUILD}; do
		eval local mkubifs_args=\"\$MKUBIFS_ARGS_${name}\"
		eval local ubinize_args=\"\$UBINIZE_ARGS_${name}\"

		multiubi_mkfs "${mkubifs_args}" "${ubinize_args}" "${name}"
	done
}

IMAGE_CMD:ubi () {
	multiubi_mkfs "${MKUBIFS_ARGS}" "${UBINIZE_ARGS}"
}
IMAGE_TYPEDEP:ubi = "${UBI_IMGTYPE}"

IMAGE_CMD:ubifs = "mkfs.ubifs -r ${IMAGE_ROOTFS} -o ${IMGDEPLOYDIR}/${IMAGE_NAME}.ubifs ${MKUBIFS_ARGS}"

MIN_F2FS_SIZE ?= "524288"
IMAGE_CMD:f2fs () {
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
	dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}.f2fs seek=${size} count=0 bs=1024
	mkfs.f2fs ${EXTRA_IMAGECMD} ${IMGDEPLOYDIR}/${IMAGE_NAME}.f2fs
	sload.f2fs -f ${IMAGE_ROOTFS} ${IMGDEPLOYDIR}/${IMAGE_NAME}.f2fs
}

EXTRA_IMAGECMD = ""

inherit siteinfo kernel-arch image-artifact-names

JFFS2_ENDIANNESS ?= "${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'le', '-l', '-b', d)}"
JFFS2_ERASEBLOCK ?= "0x40000"
EXTRA_IMAGECMD:jffs2 ?= "--pad ${JFFS2_ENDIANNESS} --eraseblock=${JFFS2_ERASEBLOCK} --no-cleanmarkers"

# Change these if you want default mkfs behavior (i.e. create minimal inode number)
EXTRA_IMAGECMD:ext2 ?= "-i 4096"
EXTRA_IMAGECMD:ext3 ?= "-i 4096"
EXTRA_IMAGECMD:ext4 ?= "-i 4096"
EXTRA_IMAGECMD:btrfs ?= "-n 4096 --shrink"
EXTRA_IMAGECMD:f2fs ?= ""

# If a specific FAT size is needed, set it here (e.g. "-F 32"/"-F 16"/"-F 12")
# otherwise mkfs.vfat will automatically pick one.
EXTRA_IMAGECMD:vfat ?= ""

do_image_tar[depends] += "tar-replacement-native:do_populate_sysroot"
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
do_image_squashfs_zst[depends] += "squashfs-tools-native:do_populate_sysroot"
do_image_ubi[depends] += "mtd-utils-native:do_populate_sysroot"
do_image_ubifs[depends] += "mtd-utils-native:do_populate_sysroot"
do_image_multiubi[depends] += "mtd-utils-native:do_populate_sysroot"
do_image_f2fs[depends] += "f2fs-tools-native:do_populate_sysroot"
do_image_erofs[depends] += "erofs-utils-native:do_populate_sysroot"
do_image_erofs_lz4[depends] += "erofs-utils-native:do_populate_sysroot"
do_image_erofs_lz4hc[depends] += "erofs-utils-native:do_populate_sysroot"
do_image_vfat[depends] += "dosfstools-native:do_populate_sysroot mtools-native:do_populate_sysroot"

# This variable is available to request which values are suitable for IMAGE_FSTYPES
IMAGE_TYPES = " \
    jffs2 jffs2.sum \
    cramfs \
    ext2 ext2.gz ext2.bz2 ext2.lzma \
    ext3 ext3.gz \
    ext4 ext4.gz \
    btrfs \
    vfat \
    squashfs squashfs-xz squashfs-lzo squashfs-lz4 squashfs-zst \
    ubi ubifs multiubi \
    tar tar.gz tar.bz2 tar.xz tar.lz4 tar.zst \
    cpio cpio.gz cpio.xz cpio.lzma cpio.lz4 cpio.zst \
    wic wic.gz wic.bz2 wic.lzma wic.zst \
    container \
    f2fs \
    erofs erofs-lz4 erofs-lz4hc \
"
# These image types are x86 specific as they need syslinux
IMAGE_TYPES:append:x86 = " hddimg iso"
IMAGE_TYPES:append:x86-64 = " hddimg iso"

# Compression is a special case of conversion. The old variable
# names are still supported for backward-compatibility. When defining
# new compression or conversion commands, use CONVERSIONTYPES and
# CONVERSION_CMD/DEPENDS.
COMPRESSIONTYPES ?= ""

CONVERSIONTYPES = "gz bz2 lzma xz lz4 lzo zip 7zip zst sum md5sum sha1sum sha224sum sha256sum sha384sum sha512sum bmap u-boot vmdk vhd vhdx vdi qcow2 base64 gzsync zsync ${COMPRESSIONTYPES}"
CONVERSION_CMD:lzma = "lzma -k -f -7 ${IMAGE_NAME}.${type}"
CONVERSION_CMD:gz = "gzip -f -9 -n -c --rsyncable ${IMAGE_NAME}.${type} > ${IMAGE_NAME}.${type}.gz"
CONVERSION_CMD:bz2 = "pbzip2 -f -k ${IMAGE_NAME}.${type}"
CONVERSION_CMD:xz = "xz -f -k -c ${XZ_COMPRESSION_LEVEL} ${XZ_DEFAULTS} --check=${XZ_INTEGRITY_CHECK} ${IMAGE_NAME}.${type} > ${IMAGE_NAME}.${type}.xz"
CONVERSION_CMD:lz4 = "lz4 -f -9 -z -l ${IMAGE_NAME}.${type} ${IMAGE_NAME}.${type}.lz4"
CONVERSION_CMD:lzo = "lzop -f -9 ${IMAGE_NAME}.${type}"
CONVERSION_CMD:zip = "zip ${ZIP_COMPRESSION_LEVEL} ${IMAGE_NAME}.${type}.zip ${IMAGE_NAME}.${type}"
CONVERSION_CMD:7zip = "7za a -mx=${7ZIP_COMPRESSION_LEVEL} -mm=${7ZIP_COMPRESSION_METHOD} ${IMAGE_NAME}.${type}.${7ZIP_EXTENSION} ${IMAGE_NAME}.${type}"
CONVERSION_CMD:zst = "zstd -f -k -c ${ZSTD_DEFAULTS} ${IMAGE_NAME}.${type} > ${IMAGE_NAME}.${type}.zst"
CONVERSION_CMD:sum = "sumtool -i ${IMAGE_NAME}.${type} -o ${IMAGE_NAME}.${type}.sum ${JFFS2_SUM_EXTRA_ARGS}"
CONVERSION_CMD:md5sum = "md5sum ${IMAGE_NAME}.${type} > ${IMAGE_NAME}.${type}.md5sum"
CONVERSION_CMD:sha1sum = "sha1sum ${IMAGE_NAME}.${type} > ${IMAGE_NAME}.${type}.sha1sum"
CONVERSION_CMD:sha224sum = "sha224sum ${IMAGE_NAME}.${type} > ${IMAGE_NAME}.${type}.sha224sum"
CONVERSION_CMD:sha256sum = "sha256sum ${IMAGE_NAME}.${type} > ${IMAGE_NAME}.${type}.sha256sum"
CONVERSION_CMD:sha384sum = "sha384sum ${IMAGE_NAME}.${type} > ${IMAGE_NAME}.${type}.sha384sum"
CONVERSION_CMD:sha512sum = "sha512sum ${IMAGE_NAME}.${type} > ${IMAGE_NAME}.${type}.sha512sum"
CONVERSION_CMD:bmap = "bmaptool create ${IMAGE_NAME}.${type} -o ${IMAGE_NAME}.${type}.bmap"
CONVERSION_CMD:u-boot = "mkimage -A ${UBOOT_ARCH} -O linux -T ramdisk -C none -n ${IMAGE_NAME} -d ${IMAGE_NAME}.${type} ${IMAGE_NAME}.${type}.u-boot"
CONVERSION_CMD:vmdk = "qemu-img convert -O vmdk ${IMAGE_NAME}.${type} ${IMAGE_NAME}.${type}.vmdk"
CONVERSION_CMD:vhdx = "qemu-img convert -O vhdx -o subformat=dynamic ${IMAGE_NAME}.${type} ${IMAGE_NAME}.${type}.vhdx"
CONVERSION_CMD:vhd = "qemu-img convert -O vpc -o subformat=fixed ${IMAGE_NAME}.${type} ${IMAGE_NAME}.${type}.vhd"
CONVERSION_CMD:vdi = "qemu-img convert -O vdi ${IMAGE_NAME}.${type} ${IMAGE_NAME}.${type}.vdi"
CONVERSION_CMD:qcow2 = "qemu-img convert -O qcow2 ${IMAGE_NAME}.${type} ${IMAGE_NAME}.${type}.qcow2"
CONVERSION_CMD:base64 = "base64 ${IMAGE_NAME}.${type} > ${IMAGE_NAME}.${type}.base64"
CONVERSION_CMD:zsync = "zsyncmake_curl ${IMAGE_NAME}.${type}"
CONVERSION_CMD:gzsync = "zsyncmake_curl -z ${IMAGE_NAME}.${type}"
CONVERSION_DEPENDS_lzma = "xz-native"
CONVERSION_DEPENDS_gz = "pigz-native"
CONVERSION_DEPENDS_bz2 = "pbzip2-native"
CONVERSION_DEPENDS_xz = "xz-native"
CONVERSION_DEPENDS_lz4 = "lz4-native"
CONVERSION_DEPENDS_lzo = "lzop-native"
CONVERSION_DEPENDS_zip = "zip-native"
CONVERSION_DEPENDS_7zip = "7zip-native"
CONVERSION_DEPENDS_zst = "zstd-native"
CONVERSION_DEPENDS_sum = "mtd-utils-native"
CONVERSION_DEPENDS_bmap = "bmaptool-native"
CONVERSION_DEPENDS_u-boot = "u-boot-tools-native"
CONVERSION_DEPENDS_vmdk = "qemu-system-native"
CONVERSION_DEPENDS_vdi = "qemu-system-native"
CONVERSION_DEPENDS_qcow2 = "qemu-system-native"
CONVERSION_DEPENDS_base64 = "coreutils-native"
CONVERSION_DEPENDS_vhdx = "qemu-system-native"
CONVERSION_DEPENDS_vhd = "qemu-system-native"
CONVERSION_DEPENDS_zsync = "zsync-curl-native"
CONVERSION_DEPENDS_gzsync = "zsync-curl-native"

RUNNABLE_IMAGE_TYPES ?= "ext2 ext3 ext4"
RUNNABLE_MACHINE_PATTERNS ?= "qemu"

DEPLOYABLE_IMAGE_TYPES ?= "hddimg iso"

# The IMAGE_TYPES_MASKED variable will be used to mask out from the IMAGE_FSTYPES,
# images that will not be built at do_rootfs time: vmdk, vhd, vhdx, vdi, qcow2, hddimg, iso, etc.
IMAGE_TYPES_MASKED ?= ""

# bmap requires python3 to be in the PATH
EXTRANATIVEPATH += "${@'python3-native' if d.getVar('IMAGE_FSTYPES').find('.bmap') else ''}"
# reproducible tar requires our tar, not the host's
EXTRANATIVEPATH += "${@'tar-native' if 'tar' in d.getVar('IMAGE_FSTYPES') else ''}"
