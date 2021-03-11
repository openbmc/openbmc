SUMMARY = "Multi-purpose linux bootloader"
HOMEPAGE = "http://www.syslinux.org/"
DESCRIPTION = "The Syslinux Project covers lightweight bootloaders for MS-DOS FAT filesystems (SYSLINUX), network booting (PXELINUX), bootable "El Torito" CD-ROMs (ISOLINUX), and Linux ext2/ext3/ext4 or btrfs filesystems (EXTLINUX). The project also includes MEMDISK, a tool to boot legacy operating systems (such as DOS) from nontraditional media; it is usually used in conjunction with PXELINUX and ISOLINUX."
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
                    file://README;beginline=35;endline=41;md5=558f2c71cb1fb9ba511ccd4858e48e8a"

# If you really want to run syslinux, you need mtools.  We just want the
# ldlinux.* stuff for now, so skip mtools-native
DEPENDS = "nasm-native util-linux e2fsprogs"
PV = "6.04-pre2"

SRC_URI = "https://www.zytor.com/pub/syslinux/Testing/6.04/syslinux-${PV}.tar.xz \
           file://syslinux-remove-clean-script.patch \
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
           file://0001-install-don-t-install-obsolete-file-com32.ld.patch \
           "

SRC_URI[md5sum] = "2b31c78f087f99179feb357da312d7ec"
SRC_URI[sha256sum] = "4441a5d593f85bb6e8d578cf6653fb4ec30f9e8f4a2315a3d8f2d0a8b3fadf94"

UPSTREAM_CHECK_URI = "https://www.zytor.com/pub/syslinux/"
UPSTREAM_CHECK_REGEX = "syslinux-(?P<pver>.+)\.tar"
UPSTREAM_VERSION_UNKNOWN = "1"

COMPATIBLE_HOST = '(x86_64|i.86).*-(linux|freebsd.*)'
# Don't let the sanity checker trip on the 32 bit real mode BIOS binaries
INSANE_SKIP_${PN}-misc = "arch"
INSANE_SKIP_${PN}-chain = "arch"

EXTRA_OEMAKE = " \
	BINDIR=${bindir} SBINDIR=${sbindir} LIBDIR=${libdir} \
	DATADIR=${datadir} MANDIR=${mandir} INCDIR=${includedir} \
"

do_configure() {
	# drop win32 targets or build fails
	sed -e 's,win32/\S*,,g' -i Makefile

	# clean installer executables included in source tarball
	oe_runmake clean firmware="efi32" EFIINC="${includedir}"
	# NOTE: There is a temporary work around above to specify
	#	the efi32 as the firmware else the pre-built bios
	#	files get erased contrary to the doc/distib.txt
	#	In the future this should be "bios" and not "efi32".
}

do_compile() {
	# Make sure the recompile is OK.
	# Though the ${B} should always exist, still check it before find and rm.
	[ -d "${B}" ] && find ${B} -name '.*.d' -type f -exec rm -f {} \;

	# Rebuild only the installer; keep precompiled bootloaders
	# as per author's request (doc/distrib.txt)
	oe_runmake CC="${CC} ${CFLAGS}" \
                   LD="${LD}" LDFLAGS="${LDFLAGS}" \
                   OBJDUMP="${OBJDUMP}" \
                   OBJCOPY="${OBJCOPY}" \
                   AR="${AR}" \
                   STRIP="${STRIP}" \
                   NM="${NM}" \
                   RANLIB="${RANLIB}" \
                   firmware="bios" installer
}

do_install() {
	oe_runmake CC="${CC} ${CFLAGS}" LD="${LD}" \
                   OBJDUMP="${OBJDUMP}" \
                   OBJCOPY="${OBJCOPY}" \
                   AR="${AR}" \
                   STRIP="${STRIP}" \
                   NM="${NM}" \
                   RANLIB="${RANLIB}" \
                   firmware="bios" install INSTALLROOT="${D}"

	install -d ${D}${datadir}/syslinux/
	install -m 644 ${S}/bios/core/ldlinux.sys ${D}${datadir}/syslinux/
	install -m 644 ${S}/bios/core/ldlinux.bss ${D}${datadir}/syslinux/
	install -m 755 ${S}/bios/linux/syslinux-nomtools ${D}${bindir}/
}

PACKAGES += "${PN}-nomtools ${PN}-extlinux ${PN}-mbr ${PN}-chain ${PN}-pxelinux ${PN}-isolinux ${PN}-misc"

RDEPENDS_${PN} += "mtools"
RDEPENDS_${PN}-nomtools += "libext2fs"
RDEPENDS_${PN}-misc += "perl"

FILES_${PN} = "${bindir}/syslinux"
FILES_${PN}-nomtools = "${bindir}/syslinux-nomtools"
FILES_${PN}-extlinux = "${sbindir}/extlinux"
FILES_${PN}-mbr = "${datadir}/${BPN}/mbr.bin"
FILES_${PN}-chain = "${datadir}/${BPN}/chain.c32"
FILES_${PN}-isolinux = "${datadir}/${BPN}/isolinux.bin"
FILES_${PN}-pxelinux = "${datadir}/${BPN}/pxelinux.0"
FILES_${PN}-dev += "${datadir}/${BPN}/com32/lib*${SOLIBS} ${datadir}/${BPN}/com32/include ${datadir}/${BPN}/com32/com32.ld"
FILES_${PN}-staticdev += "${datadir}/${BPN}/com32/lib*.a ${libdir}/${BPN}/com32/lib*.a"
FILES_${PN}-misc = "${datadir}/${BPN}/* ${libdir}/${BPN}/* ${bindir}/*"

BBCLASSEXTEND = "native nativesdk"
