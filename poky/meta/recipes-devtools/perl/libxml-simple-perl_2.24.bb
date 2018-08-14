SUMMARY = "Perl module for reading and writing XML"
DESCRIPTION = "The XML::Simple Perl module provides a simple API layer \
on top of an underlying XML parsing module to maintain XML files \
(especially configuration files). It is a blunt rewrite of XML::Simple \
(by Grant McLean) to use the XML::LibXML parser for XML structures, \
where the original uses plain Perl or SAX parsers."
HOMEPAGE = "http://search.cpan.org/~markov/XML-LibXML-Simple-0.93/lib/XML/LibXML/Simple.pod"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=23477e18a0d04392cdf44ae70e49b495"
DEPENDS += "libxml-parser-perl"

SRC_URI = "http://www.cpan.org/modules/by-module/XML/XML-Simple-${PV}.tar.gz"

SRC_URI[md5sum] = "1cd2e8e3421160c42277523d5b2f4dd2"
SRC_URI[sha256sum] = "9a14819fd17c75fbb90adcec0446ceab356cab0ccaff870f2e1659205dc2424f"

S = "${WORKDIR}/XML-Simple-${PV}"

EXTRA_PERLFLAGS = "-I ${PERLHOSTLIB}"

inherit cpan ptest-perl

BBCLASSEXTEND = "native"
