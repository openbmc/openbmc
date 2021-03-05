SUMMARY = "Program for creating simple man pages"
HOMEPAGE = "https://www.gnu.org/software/help2man/"
DESCRIPTION = "help2man is a tool for automatically generating simple manual pages from program output."
SECTION = "devel"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "de8a74740bd058646567b92ab4ecdeb9da9f1a07cc7c4f607a3c14dd38d10799"

inherit autotools

# This is a hand-maintained aclocal.m4 but our autotools class currently deletes
# aclocal.m4.
EXTRA_AUTORECONF += "--exclude=aclocal"

EXTRA_OECONF = "--disable-nls"

do_install_append () {
	# Make sure we use /usr/bin/env perl
	sed -i -e "1s:#!.*:#! /usr/bin/env perl:" ${D}${bindir}/help2man
}

BBCLASSEXTEND = "native nativesdk"
