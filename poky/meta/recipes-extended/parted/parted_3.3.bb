SUMMARY = "Disk partition editing/resizing utility"
HOMEPAGE = "http://www.gnu.org/software/parted/parted.html"
DESCRIPTION = "GNU Parted manipulates partition tables. This is useful for creating space for new operating systems, reorganizing disk usage, copying data on hard disks and disk imaging."
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2f31b266d3440dd7ee50f92cf67d8e6c"
SECTION = "console/tools"
DEPENDS = "ncurses readline util-linux virtual/libiconv"

SRC_URI = "${GNU_MIRROR}/parted/parted-${PV}.tar.xz \
           file://no_check.patch \
           file://fix-doc-mandir.patch \
           file://0002-libparted_fs_resize-link-against-libuuid-explicitly-.patch \
           file://0001-Move-python-helper-scripts-used-only-in-tests-to-Pyt.patch \
           file://run-ptest \
           file://0001-libparted-fs-add-sourcedir-lib-to-include-paths.patch \
           file://0002-tests-use-skip_-rather-than-skip_test_-which-is-unde.patch \
           "

SRC_URI[md5sum] = "090655d05f3c471aa8e15a27536889ec"
SRC_URI[sha256sum] = "57e2b4bd87018625c515421d4524f6e3b55175b472302056391c5f7eccb83d44"

EXTRA_OECONF = "--disable-device-mapper"

inherit autotools pkgconfig gettext texinfo ptest

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
	for i in print-align print-max print-flags dup-clobber duplicate fs-resize; \
	  do cp ${B}/tests/.libs/$i $t/tests/; \
	done
	sed -e 's| ../parted||' -i $t/tests/*.sh
}

RDEPENDS_${PN}-ptest = "bash coreutils perl util-linux-losetup python3 make gawk e2fsprogs-mke2fs"

RDEPENDS_${PN}-ptest_append_libc-glibc = "\
        glibc-utils \
        locale-base-en-us \
        "

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "partprobe"
ALTERNATIVE_LINK_NAME[partprobe] = "${sbindir}/partprobe"
