SUMMARY = "Tools for manipulating SquashFS filesystems"
HOMEPAGE = "https://github.com/plougher/squashfs-tools"
DESCRIPTION = "Tools to create and extract Squashfs filesystems."
SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/plougher/squashfs-tools.git;protocol=https;branch=master;tag=${PV}"
SRCREV = "bad1d213ab6df587d6fa0ef7286180fbf7b86167"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"

EXTRA_OEMAKE = "${PACKAGECONFIG_CONFARGS}"

PACKAGECONFIG ??= "gzip lz4 xz zstd xattr"
PACKAGECONFIG[gzip] = "GZIP_SUPPORT=1,GZIP_SUPPORT=0,zlib"
PACKAGECONFIG[xz] = "XZ_SUPPORT=1,XZ_SUPPORT=0,xz"
PACKAGECONFIG[lzo] = "LZO_SUPPORT=1,LZO_SUPPORT=0,lzo"
PACKAGECONFIG[lz4] = "LZ4_SUPPORT=1,LZ4_SUPPORT=0,lz4"
PACKAGECONFIG[lzma] = "LZMA_XZ_SUPPORT=1,LZMA_XZ_SUPPORT=0,xz"
PACKAGECONFIG[zstd] = "ZSTD_SUPPORT=1,ZSTD_SUPPORT=0,zstd"
PACKAGECONFIG[xattr] = "XATTR_SUPPORT=1,XATTR_SUPPORT=0,attr"

do_compile() {
	oe_runmake -C ${S}/squashfs-tools all
}

do_install() {
	oe_runmake -C ${S}/squashfs-tools install INSTALL_PREFIX=${D}${prefix} INSTALL_MANPAGES_DIR=${D}${datadir}/man/man1

	install -d "${D}${includedir}"
	install -m 0644 "${S}"/squashfs-tools/squashfs_fs.h "${D}${includedir}"
}

ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"
ARM_INSTRUCTION_SET:armv6 = "arm"

BBCLASSEXTEND = "native nativesdk"

CVE_PRODUCT = "squashfs"
