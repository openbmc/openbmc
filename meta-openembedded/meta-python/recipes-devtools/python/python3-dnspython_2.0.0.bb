DESCRIPTION = "DNS toolkit for Python"
HOMEPAGE = "http://www.dnspython.org/"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5af50906b5929837f667dfe31052bd34"

SRC_URI[md5sum] = "42f922c9223dde0275e8c0695ead37da"
SRC_URI[sha256sum] = "044af09374469c3a39eeea1a146e8cac27daec951f1f1f157b1962fc7cb9d1b7"

PYPI_PACKAGE_EXT = "zip"

inherit pypi setuptools3 ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-math \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-threading \
"
