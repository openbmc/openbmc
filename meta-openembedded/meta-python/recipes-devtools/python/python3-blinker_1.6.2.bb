DESCRIPTION = "Fast, simple object-to-object and broadcast signaling."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=42cd19c88fc13d1307a4efd64ee90e4e"

SRC_URI[sha256sum] = "4afd3de66ef3a9f8067559fb7a1cbe555c17dcbe15971b05d1b625c3e7abe213"

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
