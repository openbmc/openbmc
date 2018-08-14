SUMMARY = "Perl interface to the libxml2 library"
DESCRIPTION = "This module is an interface to libxml2, providing XML and HTML parsers \
with DOM, SAX and XMLReader interfaces, a large subset of DOM Layer 3 \
interface and a XML::XPath-like interface to XPath API of libxml2. \
The module is split into several packages which are not described in this \
section; unless stated otherwise, you only need to use XML::LibXML; in \
your programs."

HOMEPAGE = "http://search.cpan.org/dist/XML-LibXML-1.99/"
SECTION = "libs"
LICENSE = "Artistic-1.0|GPLv1+"
DEPENDS += "libxml2 \
        libxml-sax-perl-native \
        zlib \
"
RDEPENDS_${PN} += "\
    libxml2 \
    libxml-sax-perl \
    libxml-sax-base-perl \
    perl-module-encode \
    perl-module-data-dumper \
    zlib \
"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/S/SH/SHLOMIF/XML-LibXML-${PV}.tar.gz;name=libxml \
    file://disable-libxml2-check.patch \
    file://fix-CATALOG-conditional-compile.patch \
    file://using-DOCB-conditional.patch \
"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=64eda1bc135f0ece1d1187f2a8ac82c1 \
    file://LICENSE;md5=97871bde150daeb5e61ad95137ff2446 \
"
SRC_URI[libxml.md5sum] = "43546fd9a3974f19323f9fb04861ece9"
SRC_URI[libxml.sha256sum] = "721452e3103ca188f5968ab06d5ba29fe8e00e49f4767790882095050312d476"

S = "${WORKDIR}/XML-LibXML-${PV}"

inherit cpan

EXTRA_CPANFLAGS = "INC=-I${STAGING_INCDIR}/libxml2 LIBS=-L${STAGING_LIBDIR}"

BBCLASSEXTEND = "native"

CFLAGS += " -D_GNU_SOURCE "
BUILD_CFLAGS += " -D_GNU_SOURCE "

FILES_${PN}-dbg =+ "${libdir}/perl/vendor_perl/*/auto/XML/LibXML/.debug/"
