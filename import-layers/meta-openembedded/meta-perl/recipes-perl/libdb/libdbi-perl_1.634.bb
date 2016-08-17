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

LIC_FILES_CHKSUM = "file://DBI.pm;beginline=8147;endline=8151;md5=d4e73f2616b2b41334cf2f7d25d827a2"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/T/TI/TIMB/DBI-${PV}.tar.gz"
SRC_URI[md5sum] = "4ad15a9c2cc9b68e3fe1f5cadf9cdb30"
SRC_URI[sha256sum] = "250712f385864818abfba409420d16d9ee61f1cc73ac85159d054a5ee86d1450"

S = "${WORKDIR}/DBI-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
