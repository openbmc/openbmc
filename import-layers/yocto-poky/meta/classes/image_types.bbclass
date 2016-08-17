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
        for i in (depstr or "").split():
            if i not in deps:
                deps.append(i)

    deps = []
    ctypes = d.getVar('COMPRESSIONTYPES', True).split()
    fstypes = set((d.getVar('IMAGE_FSTYPES', True) or "").split())
    fstypes |= set((d.getVar('IMAGE_FSTYPES_DEBUGFS', True) or "").split())
    for type in fstypes:
        if type in ["vmdk", "vdi", "qcow2", "hdddirect", "live", "iso", "hddimg"]:
            type = "ext4"
        basetype = type
        for ctype in ctypes:
            if type.endswith("." + ctype):
                basetype = type[:-len("." + ctype)]
                adddep(d.getVar("COMPRESS_DEPENDS_%s" % ctype, True), deps)
                break
        for typedepends in (d.getVar("IMAGE_TYPEDEP_%s" % basetype, True) or "").split():
            adddep(d.getVar('IMAGE_DEPENDS_%s' % typedepends, True) , deps)
        adddep(d.getVar('IMAGE_DEPENDS_%s' % basetype, True) , deps)

    depstr = ""
    for dep in deps:
        depstr += " " + dep + ":do_populate_sysroot"
    return depstr


XZ_COMPRESSION_LEVEL ?= "-e -6"
XZ_INTEGRITY_CHECK ?= "crc32"
XZ_THREADS ?= "-T 0"

JFFS2_SUM_EXTRA_ARGS ?= ""
IMAGE_CMD_jffs2 = "mkfs.jffs2 --root=${IMAGE_ROOTFS} --faketime --output=${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.jffs2 ${EXTRA_IMAGECMD}"

IMAGE_CMD_cramfs = "mkfs.cramfs ${IMAGE_ROOTFS} ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.cramfs ${EXTRA_IMAGECMD}"

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
	dd if=/dev/zero of=${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype seek=$ROOTFS_SIZE count=$COUNT bs=1024
	mkfs.$fstype -F $extra_imagecmd ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype -d ${IMAGE_ROOTFS}
}

IMAGE_CMD_ext2 = "oe_mkext234fs ext2 ${EXTRA_IMAGECMD}"
IMAGE_CMD_ext3 = "oe_mkext234fs ext3 ${EXTRA_IMAGECMD}"
IMAGE_CMD_ext4 = "oe_mkext234fs ext4 ${EXTRA_IMAGECMD}"

MIN_BTRFS_SIZE ?= "16384"
IMAGE_CMD_btrfs () {
	if [ ${ROOTFS_SIZE} -gt ${MIN_BTRFS_SIZE} ]; then
		dd if=/dev/zero of=${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.btrfs count=${ROOTFS_SIZE} bs=1024
		mkfs.btrfs ${EXTRA_IMAGECMD} -r ${IMAGE_ROOTFS} ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.btrfs
	else
		bbfatal "Rootfs is too small for BTRFS (Rootfs Actual Size: ${ROOTFS_SIZE}, BTRFS Minimum Size: ${MIN_BTRFS_SIZE})"
	fi
}

IMAGE_CMD_squashfs = "mksquashfs ${IMAGE_ROOTFS} ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.squashfs ${EXTRA_IMAGECMD} -noappend"
IMAGE_CMD_squashfs-xz = "mksquashfs ${IMAGE_ROOTFS} ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.squashfs-xz ${EXTRA_IMAGECMD} -noappend -comp xz"
IMAGE_CMD_squashfs-lzo = "mksquashfs ${IMAGE_ROOTFS} ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.squashfs-lzo ${EXTRA_IMAGECMD} -noappend -comp lzo"

# By default, tar from the host is used, which can be quite old. If
# you need special parameters (like --xattrs) which are only supported
# by GNU tar upstream >= 1.27, then override that default:
# IMAGE_CMD_TAR = "tar --xattrs --xattrs-include=*"
# IMAGE_DEPENDS_tar_append = " tar-replacement-native"
# EXTRANATIVEPATH += "tar-native"
#
# The GNU documentation does not specify whether --xattrs-include is necessary.
# In practice, it turned out to be not needed when creating archives and
# required when extracting, but it seems prudent to use it in both cases.
IMAGE_CMD_TAR ?= "tar"
IMAGE_CMD_tar = "${IMAGE_CMD_TAR} -cvf ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.tar -C ${IMAGE_ROOTFS} ."

do_image_cpio[cleandirs] += "${WORKDIR}/cpio_append"
IMAGE_CMD_cpio () {
	(cd ${IMAGE_ROOTFS} && find . | cpio -o -H newc >${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.cpio)
	if [ ! -L ${IMAGE_ROOTFS}/init -a ! -e ${IMAGE_ROOTFS}/init ]; then
		if [ -L ${IMAGE_ROOTFS}/sbin/init -o -e ${IMAGE_ROOTFS}/sbin/init ]; then
			ln -sf /sbin/init ${WORKDIR}/cpio_append/init
		else
			touch ${WORKDIR}/cpio_append/init
		fi
		(cd  ${WORKDIR}/cpio_append && echo ./init | cpio -oA -H newc -F ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.cpio)
	fi
}

ELF_KERNEL ?= "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}"
ELF_APPEND ?= "ramdisk_size=32768 root=/dev/ram0 rw console="

IMAGE_CMD_elf () {
	test -f ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.elf && rm -f ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.elf
	mkelfImage --kernel=${ELF_KERNEL} --initrd=${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.cpio.gz --output=${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.elf --append='${ELF_APPEND}' ${EXTRA_IMAGECMD}
}

IMAGE_TYPEDEP_elf = "cpio.gz"

UBI_VOLNAME ?= "${MACHINE}-rootfs"

multiubi_mkfs() {
	local mkubifs_args="$1"
	local ubinize_args="$2"
	if [ -z "$3" ]; then
		local vname=""
	else
		local vname="_$3"
	fi

	echo \[ubifs\] > ubinize${vname}-${IMAGE_NAME}.cfg
	echo mode=ubi >> ubinize${vname}-${IMAGE_NAME}.cfg
	echo image=${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${vname}${IMAGE_NAME_SUFFIX}.ubifs >> ubinize${vname}-${IMAGE_NAME}.cfg
	echo vol_id=0 >> ubinize${vname}-${IMAGE_NAME}.cfg
	echo vol_type=dynamic >> ubinize${vname}-${IMAGE_NAME}.cfg
	echo vol_name=${UBI_VOLNAME} >> ubinize${vname}-${IMAGE_NAME}.cfg
	echo vol_flags=autoresize >> ubinize${vname}-${IMAGE_NAME}.cfg
	mkfs.ubifs -r ${IMAGE_ROOTFS} -o ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${vname}${IMAGE_NAME_SUFFIX}.ubifs ${mkubifs_args}
	ubinize -o ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${vname}${IMAGE_NAME_SUFFIX}.ubi ${ubinize_args} ubinize${vname}-${IMAGE_NAME}.cfg

	# Cleanup cfg file
	mv ubinize${vname}-${IMAGE_NAME}.cfg ${DEPLOY_DIR_IMAGE}/

	# Create own symlinks for 'named' volumes
	if [ -n "$vname" ]; then
		cd ${DEPLOY_DIR_IMAGE}
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

IMAGE_CMD_ubifs = "mkfs.ubifs -r ${IMAGE_ROOTFS} -o ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.ubifs ${MKUBIFS_ARGS}"

WKS_FILE ?= "${IMAGE_BASENAME}.${MACHINE}.wks"
WKS_FILES ?= "${WKS_FILE} ${IMAGE_BASENAME}.wks"
WKS_SEARCH_PATH ?= "${THISDIR}:${@':'.join('%s/scripts/lib/wic/canned-wks' % l for l in '${BBPATH}:${COREBASE}'.split(':'))}"
WKS_FULL_PATH = "${@wks_search('${WKS_FILES}'.split(), '${WKS_SEARCH_PATH}') or ''}"

def wks_search(files, search_path):
    for f in files:
        if os.path.isabs(f):
            if os.path.exists(f):
                return f
        else:
            searched = bb.utils.which(search_path, f)
            if searched:
                return searched

IMAGE_CMD_wic () {
	out="${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}"
	wks="${WKS_FULL_PATH}"
	if [ -z "$wks" ]; then
		bbfatal "No kickstart files from WKS_FILES were found: ${WKS_FILES}. Please set WKS_FILE or WKS_FILES appropriately."
	fi

	BUILDDIR="${TOPDIR}" wic create "$wks" --vars "${STAGING_DIR_TARGET}/imgdata/" -e "${IMAGE_BASENAME}" -o "$out/"
	mv "$out/build/$(basename "${wks%.wks}")"*.direct "$out${IMAGE_NAME_SUFFIX}.wic"
	rm -rf "$out/"
}
IMAGE_CMD_wic[vardepsexclude] = "WKS_FULL_PATH WKS_FILES"

# Rebuild when the wks file or vars in WICVARS change
USING_WIC = "${@bb.utils.contains_any('IMAGE_FSTYPES', 'wic ' + ' '.join('wic.%s' % c for c in '${COMPRESSIONTYPES}'.split()), '1', '', d)}"
do_image_wic[file-checksums] += "${@'${WKS_FULL_PATH}:%s' % os.path.exists('${WKS_FULL_PATH}') if '${USING_WIC}' else ''}"

EXTRA_IMAGECMD = ""

inherit siteinfo
JFFS2_ENDIANNESS ?= "${@base_conditional('SITEINFO_ENDIANNESS', 'le', '-l', '-b', d)}"
JFFS2_ERASEBLOCK ?= "0x40000"
EXTRA_IMAGECMD_jffs2 ?= "--pad ${JFFS2_ENDIANNESS} --eraseblock=${JFFS2_ERASEBLOCK} --no-cleanmarkers"

# Change these if you want default mkfs behavior (i.e. create minimal inode number)
EXTRA_IMAGECMD_ext2 ?= "-i 4096"
EXTRA_IMAGECMD_ext3 ?= "-i 4096"
EXTRA_IMAGECMD_ext4 ?= "-i 4096"
EXTRA_IMAGECMD_btrfs ?= "-n 4096"
EXTRA_IMAGECMD_elf ?= ""

IMAGE_DEPENDS = ""
IMAGE_DEPENDS_jffs2 = "mtd-utils-native"
IMAGE_DEPENDS_cramfs = "util-linux-native"
IMAGE_DEPENDS_ext2 = "e2fsprogs-native"
IMAGE_DEPENDS_ext3 = "e2fsprogs-native"
IMAGE_DEPENDS_ext4 = "e2fsprogs-native"
IMAGE_DEPENDS_btrfs = "btrfs-tools-native"
IMAGE_DEPENDS_squashfs = "squashfs-tools-native"
IMAGE_DEPENDS_squashfs-xz = "squashfs-tools-native"
IMAGE_DEPENDS_squashfs-lzo = "squashfs-tools-native"
IMAGE_DEPENDS_elf = "virtual/kernel mkelfimage-native"
IMAGE_DEPENDS_ubi = "mtd-utils-native"
IMAGE_DEPENDS_ubifs = "mtd-utils-native"
IMAGE_DEPENDS_multiubi = "mtd-utils-native"
IMAGE_DEPENDS_wic = "parted-native"

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
    squashfs squashfs-xz squashfs-lzo \
    ubi ubifs multiubi \
    tar tar.gz tar.bz2 tar.xz tar.lz4 \
    cpio cpio.gz cpio.xz cpio.lzma cpio.lz4 \
    vmdk \
    vdi \
    qcow2 \
    hdddirect \
    elf \
    wic wic.gz wic.bz2 wic.lzma \
"

COMPRESSIONTYPES = "gz bz2 lzma xz lz4 sum md5sum sha1sum sha224sum sha256sum sha384sum sha512sum"
COMPRESS_CMD_lzma = "lzma -k -f -7 ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}"
COMPRESS_CMD_gz = "gzip -f -9 -c ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.gz"
COMPRESS_CMD_bz2 = "pbzip2 -f -k ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}"
COMPRESS_CMD_xz = "xz -f -k -c ${XZ_COMPRESSION_LEVEL} ${XZ_THREADS} --check=${XZ_INTEGRITY_CHECK} ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.xz"
COMPRESS_CMD_lz4 = "lz4c -9 -c ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.lz4"
COMPRESS_CMD_sum = "sumtool -i ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} -o ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sum ${JFFS2_SUM_EXTRA_ARGS}"
COMPRESS_CMD_md5sum = "md5sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.md5sum"
COMPRESS_CMD_sha1sum = "sha1sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sha1sum"
COMPRESS_CMD_sha224sum = "sha224sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sha224sum"
COMPRESS_CMD_sha256sum = "sha256sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sha256sum"
COMPRESS_CMD_sha384sum = "sha384sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sha384sum"
COMPRESS_CMD_sha512sum = "sha512sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sha512sum"
COMPRESS_DEPENDS_lzma = "xz-native"
COMPRESS_DEPENDS_gz = ""
COMPRESS_DEPENDS_bz2 = "pbzip2-native"
COMPRESS_DEPENDS_xz = "xz-native"
COMPRESS_DEPENDS_lz4 = "lz4-native"
COMPRESS_DEPENDS_sum = "mtd-utils-native"

RUNNABLE_IMAGE_TYPES ?= "ext2 ext3 ext4"
RUNNABLE_MACHINE_PATTERNS ?= "qemu"

DEPLOYABLE_IMAGE_TYPES ?= "hddimg iso" 

# Use IMAGE_EXTENSION_xxx to map image type 'xxx' with real image file extension name(s) for Hob
IMAGE_EXTENSION_live = "hddimg iso"

# The IMAGE_TYPES_MASKED variable will be used to mask out from the IMAGE_FSTYPES,
# images that will not be built at do_rootfs time: vmdk, vdi, qcow2, hdddirect, hddimg, iso, etc.
IMAGE_TYPES_MASKED ?= ""

# The WICVARS variable is used to define list of bitbake variables used in wic code
# variables from this list is written to <image>.env file
WICVARS ?= "BBLAYERS DEPLOY_DIR_IMAGE HDDDIR IMAGE_BASENAME IMAGE_BOOT_FILES IMAGE_LINK_NAME IMAGE_ROOTFS INITRAMFS_FSTYPES INITRD ISODIR MACHINE_ARCH ROOTFS_SIZE STAGING_DATADIR STAGING_DIR_NATIVE STAGING_LIBDIR TARGET_SYS"
