SUMMARY = "Backport of functools.lru_cache from Python 3.3"
HOMEPAGE = "https://github.com/jaraco/backports.functools_lru_cache"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a7126e068206290f3fe9f8d6c713ea6"

PYPI_PACKAGE = "backports.functools_lru_cache"

SRC_URI[sha256sum] = "d5ed2169378b67d3c545e5600d363a923b09c456dab1593914935a68ad478271"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

inherit pypi setuptools3 ptest

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

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-threading \
    "
