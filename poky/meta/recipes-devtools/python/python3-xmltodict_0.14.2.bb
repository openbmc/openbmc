SUMMARY = "Makes working with XML feel like you are working with JSON"
HOMEPAGE = "https://github.com/martinblech/xmltodict"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=01441d50dc74476db58a41ac10cb9fa2"

SRC_URI[sha256sum] = "201e7c28bb210e374999d1dde6382923ab0ed1a8a5faeece48ab525b7810a553"

PYPI_PACKAGE = "xmltodict"

BBCLASSEXTEND = "native nativesdk"

inherit pypi setuptools3 ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN} += " \
	python3-core \
	python3-xml \
	python3-io \
"

RDEPENDS:${PN}-ptest += " \
	python3-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
