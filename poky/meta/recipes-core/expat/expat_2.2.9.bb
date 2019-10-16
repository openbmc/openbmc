SUMMARY = "A stream-oriented XML parser library"
DESCRIPTION = "Expat is an XML parser library written in C. It is a stream-oriented parser in which an application registers handlers for things the parser might find in the XML document (like start tags)"
HOMEPAGE = "http://expat.sourceforge.net/"
SECTION = "libs"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=5b8620d98e49772d95fc1d291c26aa79"

SRC_URI = "${SOURCEFORGE_MIRROR}/expat/expat-${PV}.tar.bz2 \
           file://libtool-tag.patch \
	  "

SRC_URI[md5sum] = "875a2c2ff3e8eb9e5a5cd62db2033ab5"
SRC_URI[sha256sum] = "f1063084dc4302a427dabcca499c8312b3a32a29b7d2506653ecc8f950a9a237"

inherit autotools lib_package

do_configure_prepend () {
	rm -f ${S}/conftools/libtool.m4
}

BBCLASSEXTEND = "native nativesdk"
