SUMMARY = "Program for creating simple man pages"
SECTION = "devel"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"
DEPENDS = "autoconf-native automake-native"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "c25a35b30eceb315361484b0ff1f81c924e8ee5c8881576f1ee762f001dbcd1c"

inherit autotools native

EXTRA_OECONF = "--disable-nls"

# We don't want to reconfigure things as it would require 'perlnative' to be
# used.
do_configure() {
	oe_runconf
}

do_install_append () {
	# Make sure we use /usr/bin/env perl
	sed -i -e "1s:#!.*:#! /usr/bin/env perl:" ${D}${bindir}/help2man
}
