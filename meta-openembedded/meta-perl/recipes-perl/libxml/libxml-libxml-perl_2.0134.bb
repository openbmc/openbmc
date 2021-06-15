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
SRC_URI[libxml.md5sum] = "dce687dd8b7e82d1c359fd74b1852f64"
SRC_URI[libxml.sha256sum] = "f0bca4d0c2da35d879fee4cd13f352014186cedab27ab5e191f39b5d7d4f46cf"

S = "${WORKDIR}/XML-LibXML-${PV}"

inherit cpan ptest-perl

EXTRA_CPANFLAGS = "INC=-I${STAGING_INCDIR}/libxml2 LIBS=-L${STAGING_LIBDIR}"

BBCLASSEXTEND = "native"

CFLAGS += " -D_GNU_SOURCE "
BUILD_CFLAGS += " -D_GNU_SOURCE "

FILES_${PN}-dbg =+ "${libdir}/perl/vendor_perl/*/auto/XML/LibXML/.debug/"

RDEPENDS_${PN}-ptest += " \
    liburi-perl \
    perl-module-encode-byte \
    perl-module-encode-unicode \
    perl-module-locale \
    perl-module-perlio-scalar \
    perl-module-test-more \
"

do_install_prepend() {
	# test requires "-T" (taint) command line option
	rm -rf ${B}/t/pod.t
	# this only applies to author build
	rm -rf ${B}/t/pod-files-presence.t
}

do_install_ptest() {
	cp -r ${B}/t/data ${D}${PTEST_PATH}/t/
	cp -r ${B}/t/lib ${D}${PTEST_PATH}/t/
	cp -r ${B}/example ${D}${PTEST_PATH}
	cp -r ${B}/test ${D}${PTEST_PATH}
	chown -R root:root ${D}${PTEST_PATH}
}
