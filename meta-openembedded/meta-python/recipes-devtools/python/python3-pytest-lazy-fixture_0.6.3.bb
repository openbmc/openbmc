# Copyright (C) 2022 Wind River Systems

SUMMARY = "Use your fixtures in @pytest.mark.parametrize."
HOMEPAGE = "https://github.com/tvorog/pytest-lazy-fixture"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aae6f2b7c9b3ced83e0b5bb42346d4dd"

SRC_URI[sha256sum] = "0e7d0c7f74ba33e6e80905e9bfd81f9d15ef9a790de97993e34213deb5ad10ac"

SRC_URI += "file://run-ptest \
           "


inherit ptest pypi setuptools3

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}


RDEPENDS:${PN} += "python3-core python3-pytest"
RDEPENDS:${PN}-ptest = " \
    python3-unixadmin \
    python3-unittest-automake-output \
    "
