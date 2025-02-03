SUMMARY = "Convert WSGI app to ASGI app or ASGI app to WSGI app."
HOMEPAGE = "https://github.com/abersheeran/a2wsgi"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e10d05d29ec6d8be8bfc503683f1bc9a"

inherit pypi python_setuptools_build_meta ptest

SRC_URI[sha256sum] = "fc00bab1fc792f89a8ce1b491b2ad1717b145d8caefb75d0a8586946edc97cb2"

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
        python3-starlette \
        python3-unittest-automake-output \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} += " \
        python3-asyncio \
"
