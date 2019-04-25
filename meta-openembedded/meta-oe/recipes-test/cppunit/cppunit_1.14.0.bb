DESCRIPTION = "CppUnit is the C++ port of the famous JUnit framework for unit testing. Test output is in XML for automatic testing and GUI based for supervised tests. "
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/cppunit"
LICENSE = "LGPL-2.1"
SECTION = "libs"
LIC_FILES_CHKSUM = "file://COPYING;md5=b0e9ef921ff780eb328bdcaeebec3269"

SRC_URI = " \
    http://dev-www.libreoffice.org/src/cppunit-${PV}.tar.gz \
    file://0001-doc-Makefile.am-do-not-preserve-file-flags-when-copy.patch \
"
SRC_URI[md5sum] = "7ad93022171710a541bfe4bfd8b4a381"
SRC_URI[sha256sum] = "3d569869d27b48860210c758c4f313082103a5e58219a7669b52bfd29d674780"

inherit autotools
