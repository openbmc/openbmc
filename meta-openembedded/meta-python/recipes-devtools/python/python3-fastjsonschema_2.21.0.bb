# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Fastest Python implementation of JSON schema"
HOMEPAGE = "https://github.com/seznam/python-fastjsonschema"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=18950e8362b69c0c617b42b8bd8e7532"

SRC_URI[sha256sum] = "a02026bbbedc83729da3bfff215564b71902757f33f60089f1abae193daa4771"

SRC_URI += "file://run-ptest"

inherit ptest pypi setuptools3

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

# python3-misc for timeit.py
RDEPENDS:${PN}-ptest += "\
    python3-colorama \
    python3-jsonschema \
    python3-misc \
    python3-pylint \
    python3-pytest \
    python3-pytest-benchmark \
    python3-pytest-cache \
    python3-statistics \
    python3-unittest-automake-output \
"
RDEPENDS:${PN} += "\
    python3-core \
    python3-urllib3 \
    python3-numbers \
    python3-pickle \
    python3-json \
    "

BBCLASSEXTEND = "native nativesdk"
