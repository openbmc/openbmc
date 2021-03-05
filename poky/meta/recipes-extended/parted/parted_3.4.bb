SUMMARY = "Disk partition editing/resizing utility"
HOMEPAGE = "http://www.gnu.org/software/parted/parted.html"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2f31b266d3440dd7ee50f92cf67d8e6c"
SECTION = "console/tools"
DEPENDS = "ncurses util-linux virtual/libiconv"

SRC_URI = "${GNU_MIRROR}/parted/parted-${PV}.tar.xz \
           file://no_check.patch \
           file://fix-doc-mandir.patch \
           file://0002-libparted_fs_resize-link-against-libuuid-explicitly-.patch \
           file://run-ptest \
           "

SRC_URI[md5sum] = "357d19387c6e7bc4a8a90fe2d015fe80"
SRC_URI[sha256sum] = "e1298022472da5589b7f2be1d5ee3c1b66ec3d96dfbad03dc642afd009da5342"

EXTRA_OECONF = "--disable-device-mapper"

inherit autotools pkgconfig gettext texinfo ptest

PACKAGECONFIG ?= "readline"
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
	sed -i "s|^VERSION.*|VERSION = ${PV}|g" $t/tests/Makefile
	sed -i "s|^srcdir =.*|srcdir = \.|g" $t/tests/Makefile
	sed -i "s|^abs_srcdir =.*|abs_srcdir = \.|g" $t/tests/Makefile
	sed -i "s|^abs_top_srcdir =.*|abs_top_srcdir = \.\.|g" $t/tests/Makefile
	sed -i "s|^Makefile:.*|Makefile:|g" $t/tests/Makefile
	sed -i "/^BUILDINFO.*$/d" $t/tests/Makefile
	for i in print-align print-max print-flags dup-clobber duplicate fs-resize; \
	  do cp ${B}/tests/.libs/$i $t/tests/; \
	done
	sed -e 's| ../parted||' -i $t/tests/*.sh
}

RDEPENDS_${PN}-ptest = "bash coreutils perl util-linux-losetup python3 make gawk e2fsprogs-mke2fs python3-core"
RRECOMMENDS_${PN}-ptest = "kernel-module-scsi-debug"
RDEPENDS_${PN}-ptest_append_libc-glibc = "\
        glibc-utils \
        locale-base-en-us \
        "

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "partprobe"
ALTERNATIVE_LINK_NAME[partprobe] = "${sbindir}/partprobe"
