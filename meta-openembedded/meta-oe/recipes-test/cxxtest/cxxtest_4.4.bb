DESCRIPTION = "CxxTest is a unit testing framework for C++ that is similar in spirit to JUnit, CppUnit, and xUnit."
HOMEPAGE = "http://cxxtest.com/"
SECTION = "devel"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI = "http://downloads.sourceforge.net/project/cxxtest/cxxtest/${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "1c154fef91c65dbf1cd4519af7ade70a61d85a923b6e0c0b007dc7f4895cf7d8"

UPSTREAM_CHECK_URI = "https://sourceforge.net/p/cxxtest/activity/"

inherit setuptools3

SETUPTOOLS_SETUP_PATH = "${S}/python"

do_install:append() {
    install -d ${D}${includedir}
    cp -a ${S}/cxxtest/ ${D}${includedir}
    # Fix the interpretter as otherwise this points to the build host python
    sed '1c\
#!/usr/bin/env python3' -i ${D}${bindir}/cxxtestgen
}

BBCLASSEXTEND = "native nativesdk"
