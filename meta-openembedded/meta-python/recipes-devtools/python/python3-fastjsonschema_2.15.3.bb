# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Fastest Python implementation of JSON schema"
HOMEPAGE = "https://github.com/seznam/python-fastjsonschema"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=18950e8362b69c0c617b42b8bd8e7532"

SRCREV = "d03f3835da4899bdeb597a9d3f30a709e7c3254f"
PYPI_SRC_URI = "git://github.com/horejsek/python-fastjsonschema;protocol=https;branch=master"

SRC_URI += "file://run-ptest"

inherit ptest pypi setuptools3

S = "${WORKDIR}/git"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN}-ptest += "\
    python3-colorama \
    python3-jsonschema \
    python3-pylint \
    python3-pytest \
    python3-pytest-benchmark \
    python3-pytest-cache \
"
RDEPENDS:${PN} += "\
    python3-core \
    python3-urllib3 \
    python3-numbers \
    python3-pickle \
    "

BBCLASSEXTEND = "native nativesdk"
