SUMMARY = "Base class SAX Drivers and Filters"
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

SRC_URI[md5sum] = "38c8c3247dfd080712596118d70dbe32"
SRC_URI[sha256sum] = "666270318b15f88b8427e585198abbc19bc2e6ccb36dc4c0a4f2d9807330219e"

S = "${WORKDIR}/XML-SAX-Base-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
