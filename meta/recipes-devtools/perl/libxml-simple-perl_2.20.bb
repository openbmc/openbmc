SUMMARY = "Perl module for reading and writing XML"
DESCRIPTION = "The XML::Simple Perl module provides a simple API layer \
on top of an underlying XML parsing module to maintain XML files \
(especially configuration files). It is a blunt rewrite of XML::Simple \
(by Grant McLean) to use the XML::LibXML parser for XML structures, \
where the original uses plain Perl or SAX parsers."
HOMEPAGE = "http://search.cpan.org/~markov/XML-LibXML-Simple-0.93/lib/XML/LibXML/Simple.pod"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e7dbb3e2241fac8457967053fc1a1ddb"
DEPENDS += "libxml-parser-perl"

SRC_URI = "http://www.cpan.org/modules/by-module/XML/XML-Simple-${PV}.tar.gz"

SRC_URI[md5sum] = "4d10964e123b76eca36678464daa63cd"
SRC_URI[sha256sum] = "5cff13d0802792da1eb45895ce1be461903d98ec97c9c953bc8406af7294434a"

S = "${WORKDIR}/XML-Simple-${PV}"

EXTRA_PERLFLAGS = "-I ${PERLHOSTLIB}"

inherit cpan

BBCLASSEXTEND = "native"
