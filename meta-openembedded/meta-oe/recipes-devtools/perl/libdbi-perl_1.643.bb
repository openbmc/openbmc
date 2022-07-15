SUMMARY = "The Perl Database Interface"
DESCRIPTION = "DBI is a database access Application Programming Interface \
(API) for the Perl Language. The DBI API Specification defines a set \
of functions, variables and conventions that provide a consistent \
database interface independent of the actual database being used. \
"
HOMEPAGE = "http://search.cpan.org/dist/DBI/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=10982c7148e0a012c0fd80534522f5c5"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/T/TI/TIMB/DBI-${PV}.tar.gz \
           file://CVE-2014-10402.patch \
           "
SRC_URI[md5sum] = "352f80b1e23769c116082a90905d7398"
SRC_URI[sha256sum] = "8a2b993db560a2c373c174ee976a51027dd780ec766ae17620c20393d2e836fa"

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
