SUMMARY = "Perl module for reading and writing XML"
DESCRIPTION = "The XML::Simple Perl module provides a simple API layer \
on top of an underlying XML parsing module to maintain XML files \
(especially configuration files). It is a blunt rewrite of XML::Simple \
(by Grant McLean) to use the XML::LibXML parser for XML structures, \
where the original uses plain Perl or SAX parsers."
HOMEPAGE = "http://search.cpan.org/~markov/XML-LibXML-Simple-0.93/lib/XML/LibXML/Simple.pod"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cf206df8c9fe775f1d4c484386491eac"
DEPENDS += "libxml-parser-perl"

SRC_URI = "http://www.cpan.org/modules/by-module/XML/XML-Simple-${PV}.tar.gz"

SRC_URI[md5sum] = "bb841dce889a26c89a1c2739970e9fbc"
SRC_URI[sha256sum] = "531fddaebea2416743eb5c4fdfab028f502123d9a220405a4100e68fc480dbf8"

S = "${WORKDIR}/XML-Simple-${PV}"

EXTRA_PERLFLAGS = "-I ${PERLHOSTLIB}"

inherit cpan ptest-perl

RDEPENDS:${PN} += " \
    libxml-namespacesupport-perl \
    libxml-parser-perl \
    libxml-sax-perl \
"

RDEPENDS:${PN}-ptest += " \
    perl-module-file-temp \
    perl-module-test-more \
"

BBCLASSEXTEND = "native"
