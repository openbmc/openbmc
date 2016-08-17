SUMMARY = "Program for creating simple man pages"
SECTION = "devel"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
DEPENDS = "autoconf-native automake-native"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BPN}-${PV}.tar.xz"

SRC_URI[md5sum] = "d1d44a7a7b2bd61755a2045d96ecaea0"
SRC_URI[sha256sum] = "c232af6475ef65bee02770862a362f4c4c2e6c9967d39e987eb94cadcfc13856"

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
