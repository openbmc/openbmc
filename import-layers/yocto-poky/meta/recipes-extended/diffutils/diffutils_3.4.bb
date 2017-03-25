LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

require diffutils.inc

SRC_URI = "${GNU_MIRROR}/diffutils/diffutils-${PV}.tar.xz \
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
           file://run-ptest \
"

EXTRA_OECONF += "--without-libsigsegv-prefix"

do_configure_prepend () {
	# Need to remove gettext macros with weird mix of versions
	for i in codeset.m4 gettext_gl.m4 intlmacosx.m4 inttypes-pri.m4 lib-ld_gl.m4 lib-prefix_gl.m4 po_gl.m4 ssize_t.m4 wchar_t.m4 wint_t.m4; do
		rm -f ${S}/m4/$i
	done
}

SRC_URI[md5sum] = "df9ca465f30307cffd03da176a43a5d4"
SRC_URI[sha256sum] = "db53c025f2ac3d217bcf753dad6dee7b410b33d0948495ff015aaf8b91189ce2"

inherit ptest

do_install_ptest() {
	t=${D}${PTEST_PATH}
	install -D ${S}/build-aux/test-driver $t/build-aux/test-driver
	cp -r ${S}/tests $t/
	install ${B}/tests/Makefile $t/tests/
	sed -e 's|^Makefile:|_Makefile:|' \
	    -e 's|bash|sh|' \
	    -e 's|^top_srcdir = \(.*\)|top_srcdir = ..\/|' \
	    -e 's|^srcdir = \(.*\)|srcdir = .|' \
	    -e 's|"`$(built_programs)`"|diff|' \
	    -e 's|gawk|awk|g' \
	    -i $t/tests/Makefile
}
