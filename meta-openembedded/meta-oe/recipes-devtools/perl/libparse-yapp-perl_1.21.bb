SUMMARY = "Perl extension for generating and using LALR parsers"
DESCRIPTION = "Parse::Yapp (Yet Another Perl Parser compiler) is a collection \
of modules that let you generate and use yacc like thread safe (reentrant) parsers \
with perl object oriented interface."

HOMEPAGE = "http://search.cpan.org/dist/Parse-Yapp/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://lib/Parse/Yapp.pm;beginline=508;endline=521;md5=41a4542fcde97a600c9de0d782a90256"

SRC_URI = "https://www.cpan.org/authors/id/W/WB/WBRASWELL/Parse-Yapp-${PV}.tar.gz"

SRC_URI[md5sum] = "69584d5b0f0304bb2a23cffcd982c5de"
SRC_URI[sha256sum] = "3810e998308fba2e0f4f26043035032b027ce51ce5c8a52a8b8e340ca65f13e5"

S = "${WORKDIR}/Parse-Yapp-${PV}"

inherit cpan

do_install:append() {
    sed -i "s:^#!.*:#!/usr/bin/env perl:" ${D}${bindir}/yapp
}

BBCLASSEXTEND = "native"
