DESCRIPTION = "Fast, simple object-to-object and broadcast signaling."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=42cd19c88fc13d1307a4efd64ee90e4e"

SRC_URI[sha256sum] = "152090d27c1c5c722ee7e48504b02d76502811ce02e1523553b4cf8c8b3d3a8d"

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
