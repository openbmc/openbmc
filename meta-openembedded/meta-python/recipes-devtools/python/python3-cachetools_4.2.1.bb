SUMMARY = "Extensible memoizing collections and decorators"
HOMEPAGE = "https://github.com/tkem/cachetools"
DESCRIPTION = "This module provides various memoizing \
collections and decorators, including variants of the \
Python 3 Standard Library @lru_cache function decorator."
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2d1e1bf0ccb26126a230c51f997ce362"

inherit pypi setuptools3 ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN} += " \
	${PYTHON_PN}-math \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

SRC_URI[sha256sum] = "f469e29e7aa4cff64d8de4aad95ce76de8ea1125a16c68e0d93f65c3c3dc92e9"

BBCLASSEXTEND = "native nativesdk"
