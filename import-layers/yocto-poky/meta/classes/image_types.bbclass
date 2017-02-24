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

    fstypes = set((d.getVar('IMAGE_FSTYPES', True) or "").split())
    fstypes |= set((d.getVar('IMAGE_FSTYPES_DEBUGFS', True) or "").split())

    deps = set()
    for typestring in fstypes:
        types = typestring.split(".")
        basetype, resttypes = types[0], types[1:]

        adddep(d.getVar('IMAGE_DEPENDS_%s' % basetype, True) , deps)
        for typedepends in (d.getVar("IMAGE_TYPEDEP_%s" % basetype, True) or "").split():
            adddep(d.getVar('IMAGE_DEPENDS_%s' % typedepends, True) , deps)
        for ctype in resttypes:
            adddep(d.getVar("CONVERSION_DEPENDS_%s" % ctype, True), deps)
            adddep(d.getVar("COMPRESS_DEPENDS_%s" % ctype, True), deps)

    # Sort the set so that ordering is consistant
    return " ".join(sorted(deps))

XZ_COMPRESSION_LEVEL ?= "-3"
XZ_INTEGRITY_CHECK ?= "crc32"
XZ_THREADS ?= "-T 0"

ZIP_COMPRESSION_LEVEL ?= "-9"

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
	dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype seek=$ROOTFS_SIZE count=$COUNT bs=1024
	mkfs.$fstype -F $extra_imagecmd ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype -d ${IMAGE_ROOTFS}
}

IMAGE_CMD_ext2 = "oe_mkext234fs ext2 ${EXTRA_IMAGECMD}"
IMAGE_CMD_ext3 = "oe_mkext234fs ext3 ${EXTRA_IMAGECMD}"
IMAGE_CMD_ext4 = "oe_mkext234fs ext4 ${EXTRA_IMAGECMD}"

MIN_BTRFS_SIZE ?= "16384"
IMAGE_CMD_btrfs () {
	if [ ${ROOTFS_SIZE} -gt ${MIN_BTRFS_SIZE} ]; then
		dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.btrfs count=${ROOTFS_SIZE} bs=1024
		mkfs.btrfs ${EXTRA_IMAGECMD} -r ${IMAGE_ROOTFS} ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.btrfs
	else
		bbfatal "Rootfs is too small for BTRFS (Rootfs Actual Size: ${ROOTFS_SIZE}, BTRFS Minimum Size: ${MIN_BTRFS_SIZE})"
	fi
}

IMAGE_CMD_squashfs = "mksquashfs ${IMAGE_ROOTFS} ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.squashfs ${EXTRA_IMAGECMD} -noappend"
IMAGE_CMD_squashfs-xz = "mksquashfs ${IMAGE_ROOTFS} ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.squashfs-xz ${EXTRA_IMAGECMD} -noappend -comp xz"
IMAGE_CMD_squashfs-lzo = "mksquashfs ${IMAGE_ROOTFS} ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.squashfs-lzo ${EXTRA_IMAGECMD} -noappend -comp lzo"

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
IMAGE_CMD_tar = "${IMAGE_CMD_TAR} -cvf ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.tar -C ${IMAGE_ROOTFS} ."

do_image_cpio[cleandirs] += "${WORKDIR}/cpio_append"
IMAGE_CMD_cpio () {
	(cd ${IMAGE_ROOTFS} && find . | cpio -o -H newc >${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.cpio)
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

ELF_KERNEL ?= "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}"
ELF_APPEND ?= "ramdisk_size=32768 root=/dev/ram0 rw console="

IMAGE_CMD_elf () {
	test -f ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.elf && rm -f ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.elf
	mkelfImage --kernel=${ELF_KERNEL} --initrd=${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.cpio.gz --output=${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.elf --append='${ELF_APPEND}' ${EXTRA_IMAGECMD}
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
	echo image=${IMGDEPLOYDIR}/${IMAGE_NAME}${vname}${IMAGE_NAME_SUFFIX}.ubifs >> ubinize${vname}-${IMAGE_NAME}.cfg
	echo vol_id=0 >> ubinize${vname}-${IMAGE_NAME}.cfg
	echo vol_type=dynamic >> ubinize${vname}-${IMAGE_NAME}.cfg
	echo vol_name=${UBI_VOLNAME} >> ubinize${vname}-${IMAGE_NAME}.cfg
	echo vol_flags=autoresize >> ubinize${vname}-${IMAGE_NAME}.cfg
	mkfs.ubifs -r ${IMAGE_ROOTFS} -o ${IMGDEPLOYDIR}/${IMAGE_NAME}${vname}${IMAGE_NAME_SUFFIX}.ubifs ${mkubifs_args}
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

IMAGE_CMD_ubifs = "mkfs.ubifs -r ${IMAGE_ROOTFS} -o ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.ubifs ${MKUBIFS_ARGS}"

WKS_FILE ??= "${IMAGE_BASENAME}.${MACHINE}.wks"
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

WIC_CREATE_EXTRA_ARGS ?= ""

IMAGE_CMD_wic () {
	out="${IMGDEPLOYDIR}/${IMAGE_NAME}"
	wks="${WKS_FULL_PATH}"
	if [ -z "$wks" ]; then
		bbfatal "No kickstart files from WKS_FILES were found: ${WKS_FILES}. Please set WKS_FILE or WKS_FILES appropriately."
	fi

	BUILDDIR="${TOPDIR}" wic create "$wks" --vars "${STAGING_DIR_TARGET}/imgdata/" -e "${IMAGE_BASENAME}" -o "$out/" ${WIC_CREATE_EXTRA_ARGS}
	mv "$out/build/$(basename "${wks%.wks}")"*.direct "$out${IMAGE_NAME_SUFFIX}.wic"
	rm -rf "$out/"
}
IMAGE_CMD_wic[vardepsexclude] = "WKS_FULL_PATH WKS_FILES"

# Rebuild when the wks file or vars in WICVARS change
USING_WIC = "${@bb.utils.contains_any('IMAGE_FSTYPES', 'wic ' + ' '.join('wic.%s' % c for c in '${CONVERSIONTYPES}'.split()), '1', '', d)}"
WKS_FILE_CHECKSUM = "${@'${WKS_FULL_PATH}:%s' % os.path.exists('${WKS_FULL_PATH}') if '${USING_WIC}' else ''}"
do_image_wic[file-checksums] += "${WKS_FILE_CHECKSUM}"

python () {
    if d.getVar('USING_WIC', True) and 'do_bootimg' in d:
        bb.build.addtask('do_image_wic', '', 'do_bootimg', d)
}

python do_write_wks_template () {
    """Write out expanded template contents to WKS_FULL_PATH."""
    import re

    template_body = d.getVar('_WKS_TEMPLATE', True)

    # Remove any remnant variable references left behind by the expansion
    # due to undefined variables
    expand_var_regexp = re.compile(r"\${[^{}@\n\t :]+}")
    while True:
        new_body = re.sub(expand_var_regexp, '', template_body)
        if new_body == template_body:
            break
        else:
            template_body = new_body

    wks_file = d.getVar('WKS_FULL_PATH', True)
    with open(wks_file, 'w') as f:
        f.write(template_body)
}

python () {
    if d.getVar('USING_WIC', True):
        wks_file_u = d.getVar('WKS_FULL_PATH', False)
        wks_file = d.expand(wks_file_u)
        base, ext = os.path.splitext(wks_file)
        if ext == '.in' and os.path.exists(wks_file):
            wks_out_file = os.path.join(d.getVar('WORKDIR', True), os.path.basename(base))
            d.setVar('WKS_FULL_PATH', wks_out_file)
            d.setVar('WKS_TEMPLATE_PATH', wks_file_u)
            d.setVar('WKS_FILE_CHECKSUM', '${WKS_TEMPLATE_PATH}:True')

            try:
                with open(wks_file, 'r') as f:
                    body = f.read()
            except (IOError, OSError) as exc:
                pass
            else:
                # Previously, I used expandWithRefs to get the dependency list
                # and add it to WICVARS, but there's no point re-parsing the
                # file in process_wks_template as well, so just put it in
                # a variable and let the metadata deal with the deps.
                d.setVar('_WKS_TEMPLATE', body)
                bb.build.addtask('do_write_wks_template', 'do_image_wic', None, d)
}

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

# Compression is a special case of conversion. The old variable
# names are still supported for backward-compatibility. When defining
# new compression or conversion commands, use CONVERSIONTYPES and
# CONVERSION_CMD/DEPENDS.
COMPRESSIONTYPES ?= ""

CONVERSIONTYPES = "gz bz2 lzma xz lz4 zip sum md5sum sha1sum sha224sum sha256sum sha384sum sha512sum bmap ${COMPRESSIONTYPES}"
CONVERSION_CMD_lzma = "lzma -k -f -7 ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}"
CONVERSION_CMD_gz = "gzip -f -9 -c ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.gz"
CONVERSION_CMD_bz2 = "pbzip2 -f -k ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}"
CONVERSION_CMD_xz = "xz -f -k -c ${XZ_COMPRESSION_LEVEL} ${XZ_THREADS} --check=${XZ_INTEGRITY_CHECK} ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.xz"
CONVERSION_CMD_lz4 = "lz4c -9 -c ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.lz4"
CONVERSION_CMD_zip = "zip ${ZIP_COMPRESSION_LEVEL} ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.zip ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}"
CONVERSION_CMD_sum = "sumtool -i ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} -o ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sum ${JFFS2_SUM_EXTRA_ARGS}"
CONVERSION_CMD_md5sum = "md5sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.md5sum"
CONVERSION_CMD_sha1sum = "sha1sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sha1sum"
CONVERSION_CMD_sha224sum = "sha224sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sha224sum"
CONVERSION_CMD_sha256sum = "sha256sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sha256sum"
CONVERSION_CMD_sha384sum = "sha384sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sha384sum"
CONVERSION_CMD_sha512sum = "sha512sum ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} > ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.sha512sum"
CONVERSION_CMD_bmap = "bmaptool create ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type} -o ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}.bmap"
CONVERSION_DEPENDS_lzma = "xz-native"
CONVERSION_DEPENDS_gz = ""
CONVERSION_DEPENDS_bz2 = "pbzip2-native"
CONVERSION_DEPENDS_xz = "xz-native"
CONVERSION_DEPENDS_lz4 = "lz4-native"
CONVERSION_DEPENDS_zip = "zip-native"
CONVERSION_DEPENDS_sum = "mtd-utils-native"
CONVERSION_DEPENDS_bmap = "bmap-tools-native"

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
WICVARS ?= "BBLAYERS IMGDEPLOYDIR DEPLOY_DIR_IMAGE HDDDIR IMAGE_BASENAME IMAGE_BOOT_FILES IMAGE_LINK_NAME IMAGE_ROOTFS INITRAMFS_FSTYPES INITRD ISODIR MACHINE_ARCH ROOTFS_SIZE STAGING_DATADIR STAGING_DIR_NATIVE STAGING_LIBDIR TARGET_SYS"
