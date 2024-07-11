SUMMARY = "Python Data Validation for Humans"
HOMEPAGE = "https://python-validators.github.io/validators"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b3fb4b9e6db86c69a33d5e3ee013ab59"
SRC_URI[sha256sum] = "85bc82511f6ccd0800f4c15d8c0dc546c15e369640c5ea1f24349ba0b3b17815"

inherit pypi python_setuptools_build_meta ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	python3-pytest \
	python3-unittest-automake-output \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} += " \
	python3-crypt \
	python3-datetime \
	python3-netclient \
"

BBCLASSEXTEND = "native nativesdk"
