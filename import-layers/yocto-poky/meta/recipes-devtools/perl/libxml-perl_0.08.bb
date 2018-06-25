DESCRIPTION = "libxml-perl is a collection of smaller Perl modules, scripts, and \
documents for working with XML in Perl.  libxml-perl software \
works in combination with XML::Parser, PerlSAX, XML::DOM, \
XML::Grove and others."
HOMEPAGE = "http://search.cpan.org/dist/libxml-perl/"
SUMMARY = "Collection of Perl modules for working with XML"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
PR = "r3"

LIC_FILES_CHKSUM = "file://README;beginline=33;endline=35;md5=1705549eef7577a3d6ba71123a1f0ce8"

DEPENDS += "libxml-parser-perl"

SRC_URI = "http://www.cpan.org/modules/by-module/XML/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "0ed5fbdda53d1301ddaed88db10503bb"
SRC_URI[sha256sum] = "4571059b7b5d48b7ce52b01389e95d798bf5cf2020523c153ff27b498153c9cb"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

inherit cpan ptest-perl

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

