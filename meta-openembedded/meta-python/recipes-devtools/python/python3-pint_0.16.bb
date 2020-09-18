SUMMARY = "Physical quantities module"
DESCRIPTION = "Physical quantities Python module"
HOMEPAGE = "https://github.com/hgrecco/pint"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bccf824202692270a1e0829a62e3f47b"

PYPI_PACKAGE := "Pint"

inherit pypi ptest setuptools3

SRC_URI[md5sum] = "ec78e4e42512b93f80b9093416ad7417"
SRC_URI[sha256sum] = "e437420e65fa72a2805f4036f986669c48d54944eeb0e47c8ba16f98cbdcd599"

DEPENDS += "python3-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-setuptools \
    ${PYTHON_PN}-packaging \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/testsuite
	cp -rf ${S}/pint/testsuite/* ${D}${PTEST_PATH}/testsuite/
}
