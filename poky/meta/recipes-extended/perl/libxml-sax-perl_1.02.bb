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
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
DEPENDS += "libxml-namespacesupport-perl-native"
RDEPENDS:${PN} += "libxml-namespacesupport-perl libxml-sax-base-perl perl-module-file-temp"

LIC_FILES_CHKSUM = "file://LICENSE;md5=65c4cd8f39c24c7135ed70dacbcb09e3"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/G/GR/GRANTM/XML-SAX-${PV}.tar.gz"

SRC_URI[md5sum] = "b62e3754523695c7f5bbcafa3676a38d"
SRC_URI[sha256sum] = "4506c387043aa6a77b455f00f57409f3720aa7e553495ab2535263b4ed1ea12a"

S = "${WORKDIR}/XML-SAX-${PV}"

inherit cpan ptest-perl

do_install_ptest() {
	cp -r ${B}/testfiles ${D}${PTEST_PATH}
	chown -R root:root ${D}${PTEST_PATH}/testfiles
}

RDEPENDS:${PN} += "perl-module-encode perl-module-perlio"

RDEPENDS:${PN}-ptest += " \
    perl-module-base  \
    perl-module-encode-byte \
    perl-module-encode-unicode \
    perl-module-fatal \
    perl-module-test \
"

BBCLASSEXTEND = "native nativesdk"
