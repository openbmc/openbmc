SUMMARY = "Parsing made fun ... using typing."
HOMEPAGE = "https://github.com/hgrecco/flexparser"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=32f547dac365c355d2cdbcd7ebea9144"

DEPENDS += "python3-setuptools-scm-native"
SRC_URI[sha256sum] = "36f795d82e50f5c9ae2fde1c33f21f88922fdd67b7629550a3cc4d0b40a66856"

inherit pypi python_setuptools_build_meta ptest

PYPI_PACKAGE = "flexparser"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	python3-pytest \
	python3-unittest-automake-output \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/testsuite
	cp -rf ${S}/flexparser/testsuite/* ${D}${PTEST_PATH}/testsuite/
}

RDEPENDS:${PN} += " \
	python3-compression \
	python3-email \
	python3-logging \
"
