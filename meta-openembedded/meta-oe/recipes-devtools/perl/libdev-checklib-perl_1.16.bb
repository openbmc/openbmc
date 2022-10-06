SUMMARY = "A Perl module that checks whether a particular C library and its headers are available"
DESCRIPTION = "This module provides a way of checking whether a particular library \
and its headers are available, by attempting to compile a simple program and \
link against it."
HOMEPAGE = "https://metacpan.org/pod/Devel::CheckLib"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;md5=7911cdbb572d25c5f2e2ea17f669efc2"

SRC_URI = "https://cpan.metacpan.org/modules/by-module/Devel/Devel-CheckLib-${PV}.tar.gz \
           file://0001-CheckLib.pm-don-t-execute-the-binary.patch \
"
SRC_URI[sha256sum] = "869d38c258e646dcef676609f0dd7ca90f085f56cf6fd7001b019a5d5b831fca"

S = "${WORKDIR}/Devel-CheckLib-${PV}"

inherit cpan

do_install:append() {
    # update interpreter on shebang line
    # since old version env doesn't support multiple arguments, replace option
    # '-w' with 'use warnings;'
    sed -i -e "s:^#!.*:#!/usr/bin/env perl:" \
           -e "/use strict;/ause warnings;" ${D}${bindir}/use-devel-checklib
}

BBCLASSEXTEND="native"
