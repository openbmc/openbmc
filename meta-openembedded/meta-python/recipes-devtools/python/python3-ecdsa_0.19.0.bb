SUMMARY = "ECDSA cryptographic signature library (pure python)"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=66ffc5e30f76cbb5358fe54b645e5a1d"

PYPI_PACKAGE = "ecdsa"
SRC_URI[sha256sum] = "60eaad1199659900dd0af521ed462b793bbdf867432b3948e87416ae4caf6bf8"

inherit pypi setuptools3 python3native ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	python3-hypothesis \
	python3-pytest \
	python3-unittest-automake-output \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/src/ecdsa/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} += " \
	python3-gmpy2 \
	python3-json \
	python3-six \
"

do_install:append() {
	rm ${D}${PYTHON_SITEPACKAGES_DIR}/ecdsa/test_*.py
}

BBCLASSEXTEND = "native nativesdk"
