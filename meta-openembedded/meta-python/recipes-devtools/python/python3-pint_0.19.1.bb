SUMMARY = "Physical quantities module"
DESCRIPTION = "Physical quantities Python module"
HOMEPAGE = "https://github.com/hgrecco/pint"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bccf824202692270a1e0829a62e3f47b"

PYPI_PACKAGE := "Pint"

inherit pypi ptest python_setuptools_build_meta

SRC_URI[sha256sum] = "3e5913e4ad125f672f72e19e06a8cb1f6d36a4922b6e5a49e04ff882511f82c8"

DEPENDS += "python3-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-setuptools \
    ${PYTHON_PN}-packaging \
"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/testsuite
	cp -rf ${S}/pint/testsuite/* ${D}${PTEST_PATH}/testsuite/
}
