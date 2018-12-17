SUMMARY = "Perl module for using and building Perl SAX2 XML processors"
HOMEPAGE = "http://search.cpan.org/dist/XML-SAX/"
DESCRIPTION = "XML::SAX consists of several framework classes for using and \
building Perl SAX2 XML parsers, filters, and drivers.  It is designed \ 
around the need to be able to "plug in" different SAX parsers to an \
application without requiring programmer intervention.  Those of you \
familiar with the DBI will be right at home.  Some of the designs \
come from the Java JAXP specification (SAX part), only without the \
javaness."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
DEPENDS += "libxml-namespacesupport-perl-native"
RDEPENDS_${PN} += "libxml-namespacesupport-perl libxml-sax-base-perl perl-module-file-temp"

LIC_FILES_CHKSUM = "file://LICENSE;md5=65c4cd8f39c24c7135ed70dacbcb09e3"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/G/GR/GRANTM/XML-SAX-${PV}.tar.gz"

SRC_URI[md5sum] = "861a454f7bf269990ed2c1c125f4db48"
SRC_URI[sha256sum] = "45ea6564ef8692155d57b2de0862b6442d3c7e29f4a9bc9ede5d7ecdc74c2ae3"

S = "${WORKDIR}/XML-SAX-${PV}"

inherit cpan ptest-perl

do_install_ptest() {
	cp -r ${B}/testfiles ${D}${PTEST_PATH}
	chown -R root:root ${D}${PTEST_PATH}/testfiles
}

BBCLASSEXTEND = "native"
