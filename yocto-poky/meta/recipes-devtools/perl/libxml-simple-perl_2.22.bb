SUMMARY = "Perl module for reading and writing XML"
DESCRIPTION = "The XML::Simple Perl module provides a simple API layer \
on top of an underlying XML parsing module to maintain XML files \
(especially configuration files). It is a blunt rewrite of XML::Simple \
(by Grant McLean) to use the XML::LibXML parser for XML structures, \
where the original uses plain Perl or SAX parsers."
HOMEPAGE = "http://search.cpan.org/~markov/XML-LibXML-Simple-0.93/lib/XML/LibXML/Simple.pod"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa1187fceda00eee10b62961407ea7be"
DEPENDS += "libxml-parser-perl"

SRC_URI = "http://www.cpan.org/modules/by-module/XML/XML-Simple-${PV}.tar.gz"

SRC_URI[md5sum] = "0914abddfce749453ed89b54029f2643"
SRC_URI[sha256sum] = "b9450ef22ea9644ae5d6ada086dc4300fa105be050a2030ebd4efd28c198eb49"

S = "${WORKDIR}/XML-Simple-${PV}"

EXTRA_PERLFLAGS = "-I ${PERLHOSTLIB}"

inherit cpan

BBCLASSEXTEND = "native"
