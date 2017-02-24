SUMMARY = "The Perl Database Interface"
DESCRIPTION = "DBI is a database access Application Programming Interface \
(API) for the Perl Language. The DBI API Specification defines a set \
of functions, variables and conventions that provide a consistent \
database interface independent of the actual database being used. \
"
HOMEPAGE = "http://search.cpan.org/dist/DBI/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
RDEPENDS_${PN} = " perl-module-carp \
                   perl-module-exporter \
                   perl-module-exporter-heavy \
                   perl-module-dynaloader \
"

LIC_FILES_CHKSUM = "file://DBI.pm;beginline=8147;endline=8151;md5=2e5f6cf47e5ad7b77dcb6172edc29292"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/T/TI/TIMB/DBI-${PV}.tar.gz"
SRC_URI[md5sum] = "60f291e5f015550dde71d1858dfe93ba"
SRC_URI[sha256sum] = "8f7ddce97c04b4b7a000e65e5d05f679c964d62c8b02c94c1a7d815bb2dd676c"

S = "${WORKDIR}/DBI-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
