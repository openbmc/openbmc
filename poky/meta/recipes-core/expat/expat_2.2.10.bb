SUMMARY = "A stream-oriented XML parser library"
DESCRIPTION = "Expat is an XML parser library written in C. It is a stream-oriented parser in which an application registers handlers for things the parser might find in the XML document (like start tags)"
HOMEPAGE = "http://expat.sourceforge.net/"
SECTION = "libs"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=9e2ce3b3c4c0f2670883a23bbd7c37a9"

SRC_URI = "${SOURCEFORGE_MIRROR}/expat/expat-${PV}.tar.bz2 \
           file://libtool-tag.patch \
	   file://run-ptest \
	   file://0001-Add-output-of-tests-result.patch \
	  "

SRC_URI[sha256sum] = "b2c160f1b60e92da69de8e12333096aeb0c3bf692d41c60794de278af72135a5"

EXTRA_OECMAKE_class-native += "-DEXPAT_BUILD_DOCS=OFF"

RDEPENDS_${PN}-ptest += "bash"

inherit cmake lib_package ptest

do_install_ptest_class-target() {
	install -m 755 ${B}/tests/* ${D}${PTEST_PATH}
}

BBCLASSEXTEND += "native nativesdk"
