DESCRIPTION = "CppUnit is the C++ port of the famous JUnit framework for unit testing. Test output is in XML for automatic testing and GUI based for supervised tests. "
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/cppunit"
LICENSE = "LGPL-2.1-only"
SECTION = "libs"
LIC_FILES_CHKSUM = "file://COPYING;md5=b0e9ef921ff780eb328bdcaeebec3269"

SRC_URI = " \
    http://dev-www.libreoffice.org/src/cppunit-${PV}.tar.gz \
    file://0001-doc-Makefile.am-do-not-preserve-file-flags-when-copy.patch \
"
SRC_URI[md5sum] = "9dc669e6145cadd9674873e24943e6dd"
SRC_URI[sha256sum] = "89c5c6665337f56fd2db36bc3805a5619709d51fb136e51937072f63fcc717a7"

inherit autotools

BBCLASSEXTEND = "native"
