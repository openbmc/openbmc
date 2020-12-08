DESCRIPTION = "Powerful Python library for atomic file writes"
HOMEPAGE = "https://github.com/untitaker/python-atomicwrites"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=91cc36cfafeefb7863673bcfcb1d4da4"

SRC_URI[md5sum] = "b5cc15c8f9f180a48665f9aacf91d817"
SRC_URI[sha256sum] = "ae70396ad1a434f9c7046fd2dd196fc04b12f9e91ffb859164193be8b6168a7a"

inherit pypi setuptools3 ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
	${PYTHON_PN}-unixadmin \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS_${PN} = "${PYTHON_PN}-misc"
