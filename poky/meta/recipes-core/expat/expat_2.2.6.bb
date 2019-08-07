SUMMARY = "A stream-oriented XML parser library"
DESCRIPTION = "Expat is an XML parser library written in C. It is a stream-oriented parser in which an application registers handlers for things the parser might find in the XML document (like start tags)"
HOMEPAGE = "http://expat.sourceforge.net/"
SECTION = "libs"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=5b8620d98e49772d95fc1d291c26aa79"

SRC_URI = "${SOURCEFORGE_MIRROR}/expat/expat-${PV}.tar.bz2 \
           file://autotools.patch \
           file://libtool-tag.patch \
           file://CVE-2018-20843.patch;striplevel=2 \
	  "

SRC_URI[md5sum] = "ca047ae951b40020ac831c28859161b2"
SRC_URI[sha256sum] = "17b43c2716d521369f82fc2dc70f359860e90fa440bea65b3b85f0b246ea81f2"

inherit autotools lib_package

do_configure_prepend () {
	rm -f ${S}/conftools/libtool.m4
}

BBCLASSEXTEND = "native nativesdk"
