SUMMARY = "SMPP library for python"
SECTION = "devel/python"
LICENSE = "GPLv3.0"
LIC_FILES_CHKSUM = "file://README.md;md5=56a03d0ce7e492d4b9487b8aae957efe"

PYPI_PACKAGE = "smpplib"
SRC_URI[sha256sum] = "df4139a279b35fbb42a58f2a254a9c6daf362b04a7f94e208dc120e0b8a3fd4b"

inherit pypi setuptools3 ptest

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
        ${PYTHON_PN}-pytest \
        ${PYTHON_PN}-unittest \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
	sed -i 's/mock/unittest.mock/g' ${D}${PTEST_PATH}/tests/*
}
