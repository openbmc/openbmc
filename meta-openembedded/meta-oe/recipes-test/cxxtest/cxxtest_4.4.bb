DESCRIPTION = "CxxTest is a unit testing framework for C++ that is similar in spirit to JUnit, CppUnit, and xUnit."
HOMEPAGE = "http://cxxtest.com/"
SECTION = "devel"
LICENSE = "LGPL-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/cxxtest-${PV}/COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI = "http://downloads.sourceforge.net/project/cxxtest/cxxtest/${PV}/cxxtest-${PV}.tar.gz"
SRC_URI[md5sum] = "c3cc3355e2ac64e34c215f28e44cfcec"
SRC_URI[sha256sum] = "1c154fef91c65dbf1cd4519af7ade70a61d85a923b6e0c0b007dc7f4895cf7d8"

S = "${WORKDIR}/cxxtest-${PV}/python"

inherit distutils3

do_install_append() {
    install -d ${D}${includedir}
    cp -a ../cxxtest ${D}${includedir}
    sed '1c\
#!/usr/bin/env python' -i ${D}${bindir}/cxxtestgen
}

BBCLASSEXTEND = "native nativesdk"
