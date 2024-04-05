SUMMARY = "Convert WSGI app to ASGI app or ASGI app to WSGI app."
HOMEPAGE = "https://github.com/abersheeran/a2wsgi"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e10d05d29ec6d8be8bfc503683f1bc9a"

inherit pypi python_setuptools_build_meta ptest

SRC_URI[sha256sum] = "50e81ac55aa609fa2c666e42bacc25c424c8884ce6072f1a7e902114b7ee5d63"

DEPENDS += " \
        python3-pdm-native \
        python3-pdm-backend-native \
"

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
        python3-httpx \
        python3-pytest \
        python3-pytest-asyncio \
        python3-unittest-automake-output \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} += " \
        python3-asyncio \
"
