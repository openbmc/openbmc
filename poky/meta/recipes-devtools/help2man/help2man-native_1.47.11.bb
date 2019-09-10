SUMMARY = "Program for creating simple man pages"
SECTION = "devel"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
DEPENDS = "autoconf-native automake-native"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "dd322adcaede5bd5eaed075ff8971bc1"
SRC_URI[sha256sum] = "5985b257f86304c8791842c0c807a37541d0d6807ee973000cf8a3fe6ad47b88"

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
