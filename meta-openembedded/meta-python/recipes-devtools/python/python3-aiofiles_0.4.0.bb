SUMMARY = "File support for asyncio"
DESCRIPTION = "Asynchronous local file IO library for asyncio and Python"
HOMEPAGE = "https://github.com/aio-libs/aiohttp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

SRC_URI[md5sum] = "cb33cf96c371fbd56fc27ab0bd81bd61"
SRC_URI[sha256sum] = "021ea0ba314a86027c166ecc4b4c07f2d40fc0f4b3a950d1868a0f2571c2bbee"

PYPI_PACKAGE = "aiofiles"

inherit pypi setuptools3

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-asyncio \
"
