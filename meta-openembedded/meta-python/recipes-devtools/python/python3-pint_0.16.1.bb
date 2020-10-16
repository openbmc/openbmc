SUMMARY = "Physical quantities module"
DESCRIPTION = "Physical quantities Python module"
HOMEPAGE = "https://github.com/hgrecco/pint"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bccf824202692270a1e0829a62e3f47b"

PYPI_PACKAGE := "Pint"

inherit pypi ptest setuptools3

SRC_URI[md5sum] = "d4a7bbdf505dee964eb1e5e6e7f80c34"
SRC_URI[sha256sum] = "d43a2e9ae003164978b60fdf8cd920d8581e1a5991df8dded29b00f4850ec83a"

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
