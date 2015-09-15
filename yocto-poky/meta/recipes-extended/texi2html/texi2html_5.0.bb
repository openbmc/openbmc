SUMMARY = "Perl script that converts Texinfo to HTML"
HOMEPAGE    = "http://www.nongnu.org/texi2html/"
SECTION     = "console/utils"
LICENSE     = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

PR = "r2"

SRC_URI     = "${SAVANNAH_GNU_MIRROR}/texi2html/${BPN}-${PV}.tar.bz2 \
               "

SRC_URI[md5sum] = "f15ac876fcdc8be865b16535f480aa54"
SRC_URI[sha256sum] = "e8a98b0ee20c495a6ab894398a065ef580272dbd5a15b1b19e8bd1bc89d9f9fa"

inherit autotools gettext texinfo

do_configure_prepend() {
	# autotools_do_configure updates po/Makefile.in.in, we also need
	# update po_document.
	cp -f ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in ${S}/po_document/
	cp -f ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in ${S}/po_messages/
}

do_install_append () {
	sed -i -e '1s,#!.*perl,#! ${USRBINPATH}/env perl,' ${D}${bindir}/texi2html
}

FILES_${PN}-doc += "${datadir}/texinfo"
