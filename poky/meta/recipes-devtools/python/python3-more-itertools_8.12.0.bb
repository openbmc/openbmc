DESCRIPTION = "More routines for operating on iterables, beyond itertools"
HOMEPAGE = "https://github.com/erikrose/more-itertools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3396ea30f9d21389d7857719816f83b5"

SRC_URI[sha256sum] = "7dc6ad46f05f545f900dd59e8dfb4e84a4827b97b3cfecb175ea0c7d247f6064"

inherit pypi setuptools3 ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN} += " \
        ${PYTHON_PN}-asyncio \
        "

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
        "

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
