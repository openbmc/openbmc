DESCRIPTION = "Fast, simple object-to-object and broadcast signaling."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=42cd19c88fc13d1307a4efd64ee90e4e"

SRC_URI[sha256sum] = "923e5e2f69c155f2cc42dafbbd70e16e3fde24d2d4aa2ab72fbe386238892462"

inherit pypi setuptools3 ptest

SRC_URI += "file://run-ptest"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
