# Note, we can probably remove the lzma option as it has be replaced with xz,
# and I don't think the kernel supports it any more.
SUMMARY = "Tools for manipulating SquashFS filesystems"
SECTION = "base"
LICENSE = "GPL-2"
LIC_FILES_CHKSUM = "file://../COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

PV = "4.3+gitr${SRCPV}"
SRCREV = "9c1db6d13a51a2e009f0027ef336ce03624eac0d"
SRC_URI = "git://github.com/plougher/squashfs-tools.git;protocol=https \
           file://0001-mksquashfs.c-get-inline-functions-work-with-C99.patch;striplevel=2 \
           file://squashfs-tools-4.3-sysmacros.patch;striplevel=2 \
           file://fix-compat.patch \
           file://0001-squashfs-tools-patch-for-CVE-2015-4645-6.patch;striplevel=2 \
"
UPSTREAM_CHECK_COMMITS = "1"
SRC_URI[lzma.md5sum] = "29d5ffd03a5a3e51aef6a74e9eafb759"
SRC_URI[lzma.sha256sum] = "c935fd04dd8e0e8c688a3078f3675d699679a90be81c12686837e0880aa0fa1e"

S = "${WORKDIR}/git/squashfs-tools"

EXTRA_OEMAKE = "${PACKAGECONFIG_CONFARGS}"

PACKAGECONFIG ??= "gzip xz lzo lz4 lzma xattr"
PACKAGECONFIG[gzip] = "GZIP_SUPPORT=1,GZIP_SUPPORT=0,zlib"
PACKAGECONFIG[xz] = "XZ_SUPPORT=1,XZ_SUPPORT=0,xz"
PACKAGECONFIG[lzo] = "LZO_SUPPORT=1,LZO_SUPPORT=0,lzo"
PACKAGECONFIG[lz4] = "LZ4_SUPPORT=1,LZ4_SUPPORT=0,lz4"
PACKAGECONFIG[lzma] = "LZMA_XZ_SUPPORT=1,LZMA_XZ_SUPPORT=0,xz"
PACKAGECONFIG[xattr] = "XATTR_SUPPORT=1,XATTR_SUPPORT=0,attr"

do_compile() {
	oe_runmake mksquashfs unsquashfs
}

do_install () {
	install -d ${D}${sbindir}
	install -m 0755 mksquashfs ${D}${sbindir}/
	install -m 0755 unsquashfs ${D}${sbindir}/
}

ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"
ARM_INSTRUCTION_SET_armv6 = "arm"

BBCLASSEXTEND = "native nativesdk"
