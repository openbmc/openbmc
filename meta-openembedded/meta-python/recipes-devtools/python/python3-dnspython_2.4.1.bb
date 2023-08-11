DESCRIPTION = "DNS toolkit for Python"
HOMEPAGE = "http://www.dnspython.org/"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5af50906b5929837f667dfe31052bd34"

SRC_URI[sha256sum] = "c33971c79af5be968bb897e95c2448e11a645ee84d93b265ce0b7aabe5dfdca8"

inherit pypi python_poetry_core ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

DEPENDS += "\
    ${PYTHON_PN}-wheel-native \
    ${PYTHON_PN}-setuptools-scm-native \
"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-math \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-threading \
"
