SUMMARY = "Makes working with XML feel like you are working with JSON"
AUTHOR = "Martin Blech"
HOMEPAGE = "https://github.com/martinblech/xmltodict"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=01441d50dc74476db58a41ac10cb9fa2"

SRC_URI[md5sum] = "ddb2bd078cef4f7e3021a578034ad941"
SRC_URI[sha256sum] = "50d8c638ed7ecb88d90561beedbf720c9b4e851a9fa6c47ebd64e99d166d8a21"

PYPI_PACKAGE = "xmltodict"

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
