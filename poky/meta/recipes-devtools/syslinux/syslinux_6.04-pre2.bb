SUMMARY = "Multi-purpose linux bootloader"
HOMEPAGE = "http://www.syslinux.org/"
DESCRIPTION = "The Syslinux Project covers lightweight bootloaders for MS-DOS FAT filesystems (SYSLINUX), network booting (PXELINUX), bootable "El Torito" CD-ROMs (ISOLINUX), and Linux ext2/ext3/ext4 or btrfs filesystems (EXTLINUX). The project also includes MEMDISK, a tool to boot legacy operating systems (such as DOS) from nontraditional media; it is usually used in conjunction with PXELINUX and ISOLINUX."
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
                    file://README;beginline=35;endline=41;md5=558f2c71cb1fb9ba511ccd4858e48e8a"

DEPENDS = "nasm-native util-linux e2fsprogs"

SRC_URI = "https://www.zytor.com/pub/syslinux/Testing/6.04/syslinux-${PV}.tar.xz \
           file://0001-linux-syslinux-support-ext2-3-4-device.patch \
           file://0002-linux-syslinux-implement-open_ext2_fs.patch \
           file://0003-linux-syslinux-implement-install_to_ext2.patch \
           file://0004-linux-syslinux-add-ext_file_read-and-ext_file_write.patch \
           file://0005-linux-syslinux-implement-handle_adv_on_ext.patch \
           file://0006-linux-syslinux-implement-write_to_ext-and-add-syslin.patch \
           file://0007-linux-syslinux-implement-ext_construct_sectmap_fs.patch \
           file://0008-libinstaller-syslinuxext-implement-syslinux_patch_bo.patch \
           file://0009-linux-syslinux-implement-install_bootblock.patch \
           file://0010-Workaround-multiple-definition-of-symbol-errors.patch \
           file://0011-install-don-t-install-obsolete-file-com32.ld.patch \
           file://0012-libinstaller-Fix-build-with-glibc-2.36.patch \
           file://0013-remove-clean-script.patch \
           file://0014-Fix-reproducibility-issues.patch \
           file://0001-ext2_fs.h-do-not-carry-an-outdated-copy.patch \
           "

SRC_URI[md5sum] = "2b31c78f087f99179feb357da312d7ec"
SRC_URI[sha256sum] = "4441a5d593f85bb6e8d578cf6653fb4ec30f9e8f4a2315a3d8f2d0a8b3fadf94"

# remove at next version upgrade or when output changes

RECIPE_NO_UPDATE_REASON = "6.04-pre3 is broken"
UPSTREAM_CHECK_URI = "https://www.zytor.com/pub/syslinux/"
UPSTREAM_CHECK_REGEX = "syslinux-(?P<pver>.+)\.tar"
UPSTREAM_VERSION_UNKNOWN = "1"

# We can build the native parts anywhere, but the target has to be x86
COMPATIBLE_HOST:class-target = '(x86_64|i.86).*-(linux|freebsd.*)'

# Don't let the sanity checker trip on the 32 bit real mode BIOS binaries
INSANE_SKIP:${PN}-misc = "arch"
INSANE_SKIP:${PN}-chain = "arch"

# When building the installer, CC is used to link. When building the bootloader,
# LD is used. However, these variables assume that GCC is used and break the
# build, so unset them.
TARGET_LDFLAGS = ""
SECURITY_LDFLAGS = ""
LDFLAGS_SECTION_REMOVAL = ""

CFLAGS += "-DNO_INLINE_FUNCS -Wno-error=implicit-function-declaration"

EXTRA_OEMAKE = " \
	BINDIR=${bindir} SBINDIR=${sbindir} LIBDIR=${libdir} \
	DATADIR=${datadir} MANDIR=${mandir} INCDIR=${includedir} \
	CC="${CC} ${CFLAGS} ${LDFLAGS}" \
	LD="${LD} ${LDFLAGS}" \
	OBJDUMP="${OBJDUMP}" \
	OBJCOPY="${OBJCOPY}" \
	AR="${AR}" \
	STRIP="${STRIP}" \
	NM="${NM}" \
	RANLIB="${RANLIB}" \
"

# mtools allows non-root users to install syslinux
PACKAGECONFIG ??= "mtools"
PACKAGECONFIG[mtools] = ",,,"

#
# Tasks for native/nativesdk which just build the installer.
#
do_configure() {
	oe_runmake firmware="bios" clean
}

do_compile() {
	oe_runmake firmware="bios" installer
}

do_install() {
	install -d ${D}${bindir}
	install \
		${B}/bios/extlinux/extlinux \
		${B}/bios/utils/isohybrid \
		${D}${bindir}

	if ${@bb.utils.contains("PACKAGECONFIG", "mtools", "true", "false", d)}; then
		install ${B}/bios/mtools/syslinux ${D}${bindir}
	else
		install ${B}/bios/linux/syslinux ${D}${bindir}
	fi
}

#
# Tasks for target which ship the precompiled bootloader and installer
#
do_configure:class-target() {
	# No need to do anything as we're mostly shipping the precompiled binaries
	:
}

do_compile:class-target() {
	# No need to do anything as we're mostly shipping the precompiled binaries
	:
}

do_install:class-target() {
	oe_runmake firmware="bios" install INSTALLROOT="${D}"

	install -d ${D}${datadir}/syslinux/
	install -m 644 ${S}/bios/core/ldlinux.sys ${D}${datadir}/syslinux/
	install -m 644 ${S}/bios/core/ldlinux.bss ${D}${datadir}/syslinux/
}

PACKAGES += "${PN}-extlinux ${PN}-mbr ${PN}-chain ${PN}-pxelinux ${PN}-isolinux ${PN}-misc"

RDEPENDS:${PN} += "${@bb.utils.contains("PACKAGECONFIG", "mtools", "mtools", "", d)}"
RDEPENDS:${PN}-misc += "perl"

FILES:${PN} = "${bindir}/syslinux"
FILES:${PN}-extlinux = "${sbindir}/extlinux"
FILES:${PN}-mbr = "${datadir}/${BPN}/mbr.bin"
FILES:${PN}-chain = "${datadir}/${BPN}/chain.c32"
FILES:${PN}-isolinux = "${datadir}/${BPN}/isolinux.bin"
FILES:${PN}-pxelinux = "${datadir}/${BPN}/pxelinux.0"
FILES:${PN}-dev += "${datadir}/${BPN}/com32/lib*${SOLIBS} ${datadir}/${BPN}/com32/include ${datadir}/${BPN}/com32/com32.ld"
FILES:${PN}-staticdev += "${datadir}/${BPN}/com32/lib*.a ${libdir}/${BPN}/com32/lib*.a"
FILES:${PN}-misc = "${datadir}/${BPN}/* ${libdir}/${BPN}/* ${bindir}/*"

BBCLASSEXTEND = "native nativesdk"
