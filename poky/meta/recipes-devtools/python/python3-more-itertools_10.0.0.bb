SUMMARY = "More routines for operating on iterables, beyond itertools"
HOMEPAGE = "https://github.com/erikrose/more-itertools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3396ea30f9d21389d7857719816f83b5"

SRC_URI[sha256sum] = "cd65437d7c4b615ab81c0640c0480bc29a550ea032891977681efd28344d51e1"

inherit pypi python_flit_core ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN} += " \
        ${PYTHON_PN}-asyncio \
        "

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-statistics \
	${PYTHON_PN}-pytest \
	${PYTHON_PN}-unittest-automake-output \
        "

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
