SUMMARY = "A stream-oriented XML parser library"
DESCRIPTION = "Expat is an XML parser library written in C. It is a stream-oriented parser in which an application registers handlers for things the parser might find in the XML document (like start tags)"
HOMEPAGE = "http://expat.sourceforge.net/"
SECTION = "libs"
LICENSE = "MIT"

SRC_URI = "${SOURCEFORGE_MIRROR}/expat/expat-${PV}.tar.gz \
           file://autotools.patch \
           file://expat-CVE-2015-1283.patch \
	  "

inherit autotools lib_package gzipnative

# This package uses an archive format known to have issue with some
# versions of gzip
do_unpack[depends] += "gzip-native:do_populate_sysroot"

do_configure_prepend () {
	rm -f ${S}/conftools/libtool.m4
}

BBCLASSEXTEND = "native nativesdk"
