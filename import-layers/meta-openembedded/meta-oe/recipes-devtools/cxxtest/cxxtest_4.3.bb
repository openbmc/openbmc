DESCRIPTION = "CxxTest is a unit testing framework for C++ that is similar in spirit to JUnit, CppUnit, and xUnit."
HOMEPAGE = "http://cxxtest.com/"
LICENSE = "LGPL-2.0"
SECTION = "devel"

SRC_URI = "http://downloads.sourceforge.net/project/cxxtest/cxxtest/${PV}/cxxtest-${PV}.tar.gz"
SRC_URI[md5sum] = "b3a24b3e1aad9acf6adac37f4c3f83ec"
SRC_URI[sha256sum] = "356d0f4810e8eb5c344147a0cca50fc0d84122c286e7644b61cb365c2ee22083"
LIC_FILES_CHKSUM = "file://${WORKDIR}/cxxtest-${PV}/COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02"
S = "${WORKDIR}/cxxtest-${PV}/python"

inherit distutils

do_install_append() {
    install -d ${D}${includedir}
    cp -a ../cxxtest ${D}${includedir}
    sed '1c\
#!/usr/bin/env python' -i ${D}${bindir}/cxxtestgen
}

BBCLASSEXTEND = "native nativesdk"
