SUMMARY = "Program for creating simple man pages"
HOMEPAGE = "https://www.gnu.org/software/help2man/"
DESCRIPTION = "help2man is a tool for automatically generating simple manual pages from program output."
SECTION = "devel"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "fd99a664ec4be9a86a0dd89719989f14f367a9c079d75d0e1d71e18a7bb51b03"

inherit autotools

# This is a hand-maintained aclocal.m4 but our autotools class currently deletes
# aclocal.m4.
EXTRA_AUTORECONF += "--exclude=aclocal"

EXTRA_OECONF = "--disable-nls"

do_install:append () {
	# Make sure we use /usr/bin/env perl
	sed -i -e "1s:#!.*:#! /usr/bin/env perl:" ${D}${bindir}/help2man
}

BBCLASSEXTEND = "native nativesdk"
