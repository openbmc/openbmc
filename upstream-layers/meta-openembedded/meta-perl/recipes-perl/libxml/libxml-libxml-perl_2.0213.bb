SUMMARY = "Perl interface to the libxml2 library"
DESCRIPTION = "This module is an interface to libxml2, providing XML and HTML parsers \
with DOM, SAX and XMLReader interfaces, a large subset of DOM Layer 3 \
interface and a XML::XPath-like interface to XPath API of libxml2. \
The module is split into several packages which are not described in this \
section; unless stated otherwise, you only need to use XML::LibXML; in \
your programs."

HOMEPAGE = "https://metacpan.org/dist/XML-LibXML"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
DEPENDS += "libxml2 \
        libxml-sax-perl-native \
        zlib \
"
RDEPENDS:${PN} += "\
    libxml2 \
    libxml-sax-perl \
    libxml-sax-base-perl \
    perl-module-encode \
    perl-module-data-dumper \
    zlib \
"

SRC_URI = "${CPAN_MIRROR}/authors/id/T/TO/TODDR/XML-LibXML-${PV}.tar.gz;name=libxml \
    file://0001-Makefile.PL-link-against-system-libxml2-without-Alien.patch \
"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=64eda1bc135f0ece1d1187f2a8ac82c1 \
    file://LICENSE;md5=97871bde150daeb5e61ad95137ff2446 \
"
SRC_URI[libxml.sha256sum] = "2af21c5d61ac34ea26a5fabf15ba5a5841e648f7189db3e33b6f28b5489802ab"

S = "${UNPACKDIR}/XML-LibXML-${PV}"

inherit cpan ptest-perl pkgconfig

BBCLASSEXTEND = "native"

CFLAGS += " -D_GNU_SOURCE "
BUILD_CFLAGS += " -D_GNU_SOURCE "

FILES:${PN}-dbg =+ "${libdir}/perl/vendor_perl/*/auto/XML/LibXML/.debug/"

RDEPENDS:${PN}-ptest += " \
    liburi-perl \
    perl-module-encode-byte \
    perl-module-encode-unicode \
    perl-module-locale \
    perl-module-perlio-scalar \
    perl-module-test-more \
"

do_install:prepend() {
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
