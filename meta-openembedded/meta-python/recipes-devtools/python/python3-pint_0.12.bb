SUMMARY = "Physical quantities module"
DESCRIPTION = "Physical quantities Python module"
HOMEPAGE = "https://github.com/hgrecco/pint"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bccf824202692270a1e0829a62e3f47b"

PYPI_PACKAGE := "Pint"

inherit pypi ptest setuptools3

SRC_URI[md5sum] = "0af699bc0ccdff56228b4a81216b4f7d"
SRC_URI[sha256sum] = "dc899061f9dc478e0aac3b0d872ca33d120efd32c382984818adab3522b6c793"

DEPENDS += "python3-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/testsuite
	cp -rf ${S}/pint/testsuite/* ${D}${PTEST_PATH}/testsuite/
}
