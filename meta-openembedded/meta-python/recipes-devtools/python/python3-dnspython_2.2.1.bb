DESCRIPTION = "DNS toolkit for Python"
HOMEPAGE = "http://www.dnspython.org/"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5af50906b5929837f667dfe31052bd34"

SRC_URI[sha256sum] = "0f7569a4a6ff151958b64304071d370daa3243d15941a7beedf0c9fe5105603e"

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
