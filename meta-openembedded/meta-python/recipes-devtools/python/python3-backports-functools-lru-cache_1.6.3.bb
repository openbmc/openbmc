SUMMARY = "Backport of functools.lru_cache from Python 3.3"
HOMEPAGE = "https://github.com/jaraco/backports.functools_lru_cache"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a7126e068206290f3fe9f8d6c713ea6"

PYPI_PACKAGE = "backports.functools_lru_cache"

SRC_URI[sha256sum] = "d84e126e2a29e4fde8931ff8131240bbf30a0e7dbcc3897a8dbd8ea5ac11419c"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

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

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-threading \
    "
