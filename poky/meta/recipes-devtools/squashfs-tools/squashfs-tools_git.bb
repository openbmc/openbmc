# Note, we can probably remove the lzma option as it has be replaced with xz,
# and I don't think the kernel supports it any more.
SUMMARY = "Tools for manipulating SquashFS filesystems"
HOMEPAGE = "https://github.com/plougher/squashfs-tools"
DESCRIPTION = "Tools to create and extract Squashfs filesystems."
SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

PV = "4.6.1"
SRCREV = "d8cb82d9840330f9344ec37b992595b5d7b44184"
SRC_URI = "git://github.com/plougher/squashfs-tools.git;protocol=https;branch=v6.1.1"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "${PACKAGECONFIG_CONFARGS}"

PACKAGECONFIG ??= "gzip xz lzo lz4 lzma xattr zstd reproducible"
PACKAGECONFIG[gzip] = "GZIP_SUPPORT=1,GZIP_SUPPORT=0,zlib"
PACKAGECONFIG[xz] = "XZ_SUPPORT=1,XZ_SUPPORT=0,xz"
PACKAGECONFIG[lzo] = "LZO_SUPPORT=1,LZO_SUPPORT=0,lzo"
PACKAGECONFIG[lz4] = "LZ4_SUPPORT=1,LZ4_SUPPORT=0,lz4"
PACKAGECONFIG[lzma] = "LZMA_XZ_SUPPORT=1,LZMA_XZ_SUPPORT=0,xz"
PACKAGECONFIG[xattr] = "XATTR_SUPPORT=1,XATTR_SUPPORT=0,attr"
PACKAGECONFIG[zstd] = "ZSTD_SUPPORT=1,ZSTD_SUPPORT=0,zstd"
PACKAGECONFIG[reproducible] = "REPRODUCIBLE_DEFAULT=1,REPRODUCIBLE_DEFAULT=0,"

do_compile() {
        cd ${S}/squashfs-tools
	oe_runmake all
}

do_install() {
        cd ${S}/squashfs-tools
	install -d "${D}${includedir}"
	oe_runmake install INSTALL_PREFIX=${D}${prefix} INSTALL_MANPAGES_DIR=${D}${datadir}/man/man1
	install -m 0644 "${S}"/squashfs-tools/squashfs_fs.h "${D}${includedir}"
}

ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"
ARM_INSTRUCTION_SET:armv6 = "arm"

BBCLASSEXTEND = "native nativesdk"

CVE_PRODUCT = "squashfs"
