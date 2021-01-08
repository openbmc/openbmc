SUMMARY = "Multi-purpose linux bootloader"
HOMEPAGE = "http://www.syslinux.org/"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
                    file://README;beginline=35;endline=41;md5=558f2c71cb1fb9ba511ccd4858e48e8a"

DEPENDS = "nasm-native util-linux e2fsprogs"

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

RECIPE_NO_UPDATE_REASON = "6.04-pre3 is broken"
UPSTREAM_CHECK_URI = "https://www.zytor.com/pub/syslinux/"
UPSTREAM_CHECK_REGEX = "syslinux-(?P<pver>.+)\.tar"
UPSTREAM_VERSION_UNKNOWN = "1"

# We can build the native parts anywhere, but the target has to be x86
COMPATIBLE_HOST_class-target = '(x86_64|i.86).*-(linux|freebsd.*)'

# Don't let the sanity checker trip on the 32 bit real mode BIOS binaries
INSANE_SKIP_${PN}-misc = "arch"
INSANE_SKIP_${PN}-chain = "arch"

# When building the installer, CC is used to link. When building the bootloader,
# LD is used. However, these variables assume that GCC is used and break the
# build, so unset them.
TARGET_LDFLAGS = ""
SECURITY_LDFLAGS = ""
LDFLAGS_SECTION_REMOVAL = ""

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
		${B}/bios/mtools/syslinux \
		${B}/bios/extlinux/extlinux \
		${B}/bios/utils/isohybrid \
		${D}${bindir}
}

#
# Tasks for target which ship the precompiled bootloader and installer
#
do_configure_class-target() {
	# No need to do anything as we're mostly shipping the precompiled binaries
	:
}

do_compile_class-target() {
	# No need to do anything as we're mostly shipping the precompiled binaries
	:
}

do_install_class-target() {
	oe_runmake firmware="bios" install INSTALLROOT="${D}"

	install -d ${D}${datadir}/syslinux/
	install -m 644 ${S}/bios/core/ldlinux.sys ${D}${datadir}/syslinux/
	install -m 644 ${S}/bios/core/ldlinux.bss ${D}${datadir}/syslinux/
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
