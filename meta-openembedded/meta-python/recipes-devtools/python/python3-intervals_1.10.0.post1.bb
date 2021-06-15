DESCRIPTION = "Interval arithmetic for Python"
HOMEPAGE = "https://github.com/AlexandreDecan/python-intervals"
SECTION = "devel/python"

LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=05f1e16a8e59ce3e9a979e881816c2ab"

PYPI_PACKAGE := "python-intervals"

SRC_URI += " \
	file://run-ptest \
"

inherit pypi setuptools3 ptest

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	cp -f ${S}/test_intervals.py ${D}${PTEST_PATH}
	cp -f ${S}/README.md ${D}${PTEST_PATH}
}

SRC_URI[sha256sum] = "68a772dc2de6b2b2e83b457329ffa8f9286710994b8070db54348a05762515d2"

BBCLASSEXTEND = "native"
