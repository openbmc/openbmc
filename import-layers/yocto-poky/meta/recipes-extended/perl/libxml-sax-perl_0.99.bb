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
PR = "r2"

LIC_FILES_CHKSUM = "file://LICENSE;md5=65c4cd8f39c24c7135ed70dacbcb09e3"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/G/GR/GRANTM/XML-SAX-${PV}.tar.gz"

SRC_URI[md5sum] = "290f5375ae87fdebfdb5bc3854019f24"
SRC_URI[sha256sum] = "32b04b8e36b6cc4cfc486de2d859d87af5386dd930f2383c49347050d6f5ad84"

S = "${WORKDIR}/XML-SAX-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
