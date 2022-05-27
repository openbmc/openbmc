SUMMARY = "A simple wrapper around optparse for powerful command line utilities."
DESCRIPTION = "\
Click is a Python package for creating beautiful command line interfaces \
in a composable way with as little code as necessary. It's the "Command \
Line Interface Creation Kit". It's highly configurable but comes with \
sensible defaults out of the box."
HOMEPAGE = "http://click.pocoo.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=1fa98232fd645608937a0fdc82e999b8"

SRC_URI[sha256sum] = "7682dc8afb30297001674575ea00d1814d808d6a36af415a82bd481d37ba7b8e"

inherit pypi setuptools3 ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
	${PYTHON_PN}-terminal \
	${PYTHON_PN}-unixadmin \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
    cp -rf ${S}/setup.cfg ${D}${PTEST_PATH}/
    cp -rf ${S}/docs ${D}${PTEST_PATH}/
}

UPSTREAM_CHECK_REGEX = "click/(?P<pver>\d+(\.\d+)+)/"

CLEANBROKEN = "1"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-threading \
    "

BBCLASSEXTEND = "native nativesdk"
