DESCRIPTION = "CppUnit is the C++ port of the famous JUnit framework for unit testing. Test output is in XML for automatic testing and GUI based for supervised tests. "
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/cppunit"
LICENSE = "LGPL-2.1"
SECTION = "libs"

LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"
SRC_URI = " \
    http://dev-www.libreoffice.org/src/cppunit-${PV}.tar.gz \
    file://0001-doc-Makefile.am-do-not-preserve-file-flags-when-copy.patch \
"
SRC_URI[md5sum] = "fa9aa839145cdf860bf596532bb8af97"
SRC_URI[sha256sum] = "d5b9f3ffc9f1634d75b20b54f48c02e0817bca6afa1d5160b244889d6bff8e0f"

inherit autotools
