SUMMARY = "ECDSA cryptographic signature library (pure python)"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=66ffc5e30f76cbb5358fe54b645e5a1d"

PYPI_PACKAGE = "ecdsa"
SRC_URI[sha256sum] = "62635b0ac1ca2e027f82122b5b81cb706edc38cd91c63dda28e4f3455a2bf930"

CVE_PRODUCT = "python-ecdsa_project:python-ecdsa tlsfuzzer:ecdsa"

inherit pypi setuptools3 python3native ptest-python-pytest

RDEPENDS:${PN}-ptest += " \
	python3-hypothesis \
"

do_install_ptest:append () {
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
