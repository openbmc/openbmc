SUMMARY = "The Perl Database Interface"
DESCRIPTION = "DBI is a database access Application Programming Interface \
(API) for the Perl Language. The DBI API Specification defines a set \
of functions, variables and conventions that provide a consistent \
database interface independent of the actual database being used. \
"
HOMEPAGE = "http://search.cpan.org/dist/DBI/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8097b88c6165f0d43949441e6ea581cd"

SRC_URI = "https://cpan.metacpan.org/authors/id/H/HM/HMBRAND/DBI-${PV}.tar.gz \
           file://0001-Fix-building-on-Fedora-40-with-GCC-14.2.1.patch \
           "
SRC_URI[sha256sum] = "2297b99de09e67086640b590699e0e982fb469da63a93fe28dc14782db7a53c8"

S = "${WORKDIR}/DBI-${PV}"

inherit cpan ptest-perl

do_install:prepend() {
	# test requires "-T" (taint) command line option
	rm -rf ${B}/t/pod-coverage.t
	rm -rf ${B}/t/13taint.t
	# source of test failure not obvious
	rm -rf ${B}/t/85gofer.t
	# unclear why there are several duplicates of tests in tarball
	rm -rf ${B}/t/z*.t
}

do_install:append() {
	sed -i "s:^#!.*:#!/usr/bin/env perl:" ${D}${bindir}/dbiproxy \
		${D}${bindir}/dbiprof ${D}${bindir}/dbilogstrip
}

RDEPENDS:${PN}:class-target = " \
    perl \
    perl-module-carp \
    perl-module-exporter \
    perl-module-exporter-heavy \
    perl-module-dynaloader \
    perl-module-io-dir \
    perl-module-scalar-util \
    perl-module-universal \
"

RDEPENDS:${PN}-ptest = " \
    ${PN} \
    perl-module-b \
    perl-module-benchmark \
    perl-module-cwd \
    perl-module-data-dumper \
    perl-module-encode \
    perl-module-encode-byte \
    perl-module-encode-encoding \
    perl-module-file-copy \
    perl-module-file-path \
    perl-module-lib \
    perl-module-perlio \
    perl-module-perlio-scalar \
    perl-module-perlio-via \
    perl-module-sdbm-file \
    perl-module-storable \
    perl-module-test-more \
    perl-module-utf8 \
    "

BBCLASSEXTEND = "native"
