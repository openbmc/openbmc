SUMMARY = "A stream-oriented XML parser library"
DESCRIPTION = "Expat is an XML parser library written in C. It is a stream-oriented parser in which an application registers handlers for things the parser might find in the XML document (like start tags)"
HOMEPAGE = "http://expat.sourceforge.net/"
SECTION = "libs"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=5b8620d98e49772d95fc1d291c26aa79"

SRC_URI = "git://github.com/libexpat/libexpat.git;protocol=https \
           file://CVE-2013-0340.patch \
           file://libtool-tag.patch \
         "

SRCREV = "a7bc26b69768f7fb24f0c7976fae24b157b85b13"

inherit autotools lib_package

S = "${WORKDIR}/git/expat"

BBCLASSEXTEND = "native nativesdk"

CVE_PRODUCT = "expat libexpat"
