SUMMARY = "A stream-oriented XML parser library"
DESCRIPTION = "Expat is an XML parser library written in C. It is a stream-oriented parser in which an application registers handlers for things the parser might find in the XML document (like start tags)"
HOMEPAGE = "http://expat.sourceforge.net/"
SECTION = "libs"
LICENSE = "MIT"

SRC_URI = "${SOURCEFORGE_MIRROR}/expat/expat-${PV}.tar.bz2 \
           file://autotools.patch \
           file://libtool-tag.patch \
	  "

SRC_URI_append_class-native = " file://no_getrandom.patch"

inherit autotools lib_package

# This package uses an archive format known to have issue with some
# versions of gzip
DEPENDS += "pigz-native"
do_unpack[depends] += "pigz-native:do_populate_sysroot"

do_configure_prepend () {
	rm -f ${S}/conftools/libtool.m4
}

BBCLASSEXTEND = "native nativesdk"
