SUMMARY = "Library for parsing and serializing RDF syntaxes"
LICENSE = "GPL-2.0-only | LGPL-2.1-only | Apache-2.0"
LIC_FILES_CHKSUM = " \
    file://LICENSE.txt;md5=f7fed8b6ab9289b77f5c14f3f79572cc \
    file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
    file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
    file://LICENSE-2.0.txt;md5=3b83ef96387f14655fc854ddc3c6bd57 \
"

DEPENDS = "libxml2 libxslt curl yajl"

SRC_URI = " \
    http://download.librdf.org/source/${BPN}-${PV}.tar.gz \
    file://0001-Remove-the-access-to-entities-checked-private-symbol.patch \
"
SRC_URI[sha256sum] = "089db78d7ac982354bdbf39d973baf09581e6904ac4c92a98c5caadb3de44680"

inherit autotools pkgconfig

EXTRA_OECONF = " \
    --without-xml2-config \
    --without-curl-config \
    --without-xslt-config \
"
