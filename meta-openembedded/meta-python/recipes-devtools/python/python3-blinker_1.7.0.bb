DESCRIPTION = "Fast, simple object-to-object and broadcast signaling."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=42cd19c88fc13d1307a4efd64ee90e4e"

SRC_URI[sha256sum] = "e6820ff6fa4e4d1d8e2747c2283749c3f547e4fee112b98555cdcdae32996182"

inherit pypi python_setuptools_build_meta ptest

SRC_URI += "file://run-ptest"

RDEPENDS:${PN} += "\
        ${PYTHON_PN}-asyncio \
"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
