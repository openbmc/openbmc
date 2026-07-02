SUMMARY = "Disk partition editing/resizing utility"
HOMEPAGE = "http://www.gnu.org/software/parted/parted.html"
DESCRIPTION = "GNU Parted manipulates partition tables. This is useful for creating space for new operating systems, reorganizing disk usage, copying data on hard disks and disk imaging."
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=2f31b266d3440dd7ee50f92cf67d8e6c"
SECTION = "console/tools"
DEPENDS = "ncurses util-linux virtual/libiconv"

SRC_URI = "${GNU_MIRROR}/parted/parted-${PV}.tar.xz \
           file://fix-doc-mandir.patch \
           file://run-ptest \
           "

SRC_URI[sha256sum] = "008de57561a4f3c25a0648e66ed11e7b30be493889b64334a6d70f2c1951ef7b"

inherit autotools pkgconfig gettext texinfo ptest

PACKAGECONFIG ?= "readline"
PACKAGECONFIG[device-mapper] = "--enable-device-mapper,--disable-device-mapper,libdevmapper lvm2"
PACKAGECONFIG[readline] = "--with-readline,--without-readline,readline"

BBCLASSEXTEND = "native nativesdk"

do_compile_ptest() {
	oe_runmake -C tests print-align print-max dup-clobber duplicate fs-resize print-flags
}

do_install_ptest() {
	t=${D}${PTEST_PATH}
	mkdir $t/build-aux
	cp ${S}/build-aux/test-driver $t/build-aux/
	cp -r ${S}/tests $t
	cp ${B}/tests/Makefile $t/tests/
	mkdir $t/lib
	cp ${B}/lib/config.h $t/lib
	sed -e "s|^VERSION.*|VERSION = ${PV}|g" \
	    -e "s|^srcdir =.*|srcdir = \.|g" \
	    -e "s|^abs_srcdir =.*|abs_srcdir = \.|g" \
	    -e "s|^abs_top_srcdir =.*|abs_top_srcdir = "${PTEST_PATH}"|g" \
	    -e "s|^abs_top_builddir =.*|abs_top_builddir = "${PTEST_PATH}"|g" \
	    -e "/^SH_LOG_DRIVER =/s|(top_srcdir)|(top_builddir)|" \
	    -e "s|^Makefile:.*|Makefile:|g" \
	    -e "/^BUILDINFO.*$/d" \
	    -i $t/tests/Makefile
	for i in print-align print-max print-flags dup-clobber duplicate fs-resize; do
		${B}/libtool --mode=install install ${B}/tests/$i $t/tests/
	done
	sed -e 's| ../parted||' -i $t/tests/*.sh
}

RDEPENDS:${PN}-ptest = "bash coreutils perl util-linux-losetup util-linux-mkswap python3 make gawk e2fsprogs-mke2fs e2fsprogs-tune2fs python3-core dosfstools"
RRECOMMENDS:${PN}-ptest += "kernel-module-scsi-debug kernel-module-loop kernel-module-vfat"
RDEPENDS:${PN}-ptest:append:libc-glibc = "\
        glibc-utils \
        locale-base-en-us \
        "

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = "partprobe"
ALTERNATIVE_LINK_NAME[partprobe] = "${sbindir}/partprobe"
