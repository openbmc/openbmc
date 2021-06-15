SUMMARY = "Base class SAX Drivers and Filters"
HOMEPAGE = "http://search.cpan.org/dist/XML-SAX-Base/"
DESCRIPTION = "This module has a very simple task - to be a base class for \
PerlSAX drivers and filters. It's default behaviour is to pass \
the input directly to the output unchanged. It can be useful to \
use this module as a base class so you don't have to, for example, \
implement the characters() callback."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
RDEPENDS_${PN} += "perl-module-extutils-makemaker"

LIC_FILES_CHKSUM = "file://dist.ini;endline=5;md5=8f9c9a55340aefaee6e9704c88466446"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/G/GR/GRANTM/XML-SAX-Base-${PV}.tar.gz"

SRC_URI[md5sum] = "ec347a14065dd7aec7d9fb181b2d7946"
SRC_URI[sha256sum] = "66cb355ba4ef47c10ca738bd35999723644386ac853abbeb5132841f5e8a2ad0"

S = "${WORKDIR}/XML-SAX-Base-${PV}"

inherit cpan ptest-perl

RDEPENDS_${PN}-ptest += "perl-module-test perl-module-test-more"

BBCLASSEXTEND = "native nativesdk"
