DESCRIPTION = "CppUnit is the C++ port of the famous JUnit framework for unit testing. Test output is in XML for automatic testing and GUI based for supervised tests. "
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/cppunit"
LICENSE = "LGPL-2.1"
SECTION = "libs"

LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"
SRC_URI = " \
    http://dev-www.libreoffice.org/src/cppunit-${PV}.tar.gz \
    file://0001-doc-Makefile.am-do-not-preserve-file-flags-when-copy.patch \
"
SRC_URI[md5sum] = "d1c6bdd5a76c66d2c38331e2d287bc01"
SRC_URI[sha256sum] = "3f47d246e3346f2ba4d7c9e882db3ad9ebd3fcbd2e8b732f946e0e3eeb9f429f"

inherit autotools
