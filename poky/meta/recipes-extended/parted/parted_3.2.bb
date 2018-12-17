SUMMARY = "Disk partition editing/resizing utility"
HOMEPAGE = "http://www.gnu.org/software/parted/parted.html"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2f31b266d3440dd7ee50f92cf67d8e6c"
SECTION = "console/tools"
DEPENDS = "ncurses readline util-linux virtual/libiconv"
PR = "r1"

SRC_URI = "${GNU_MIRROR}/parted/parted-${PV}.tar.xz \
           file://no_check.patch \
           file://syscalls.patch \
           file://fix-doc-mandir.patch \
           file://fix-compile-failure-while-dis.patch \
           file://0001-Include-fcntl.h-in-platform_defs.h.patch \
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
           file://0002-libparted_fs_resize-link-against-libuuid-explicitly-.patch \
           file://0001-Move-python-helper-scripts-used-only-in-tests-to-Pyt.patch \
	   file://parted-3.2-sysmacros.patch \
           file://run-ptest \
           file://Makefile \
           file://0001-libparted-Use-read-only-when-probing-devices-on-linu.patch \
"

SRC_URI[md5sum] = "0247b6a7b314f8edeb618159fa95f9cb"
SRC_URI[sha256sum] = "858b589c22297cacdf437f3baff6f04b333087521ab274f7ab677cb8c6bb78e4"

EXTRA_OECONF = "--disable-device-mapper"

inherit autotools pkgconfig gettext texinfo ptest

BBCLASSEXTEND = "native"

do_compile_ptest() {
	oe_runmake -C tests print-align print-max dup-clobber duplicate fs-resize
}

do_install_ptest() {
	t=${D}${PTEST_PATH}
	mkdir $t/build-aux
	cp ${S}/build-aux/test-driver $t/build-aux/
	cp -r ${S}/tests $t
	cp ${WORKDIR}/Makefile $t/tests/
	sed -i "s|^VERSION.*|VERSION = ${PV}|g" $t/tests/Makefile
	for i in print-align print-max dup-clobber duplicate fs-resize; \
	  do cp ${B}/tests/.libs/$i $t/tests/; \
	done
	sed -e 's| ../parted||' -i $t/tests/*.sh
}

RDEPENDS_${PN}-ptest = "bash coreutils perl util-linux-losetup python3"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "partprobe"
ALTERNATIVE_LINK_NAME[partprobe] = "${sbindir}/partprobe"
