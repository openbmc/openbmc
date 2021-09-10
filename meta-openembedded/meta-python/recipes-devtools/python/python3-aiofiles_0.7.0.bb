SUMMARY = "File support for asyncio"
DESCRIPTION = "Asynchronous local file IO library for asyncio and Python"
HOMEPAGE = "https://github.com/aio-libs/aiohttp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

SRC_URI[sha256sum] = "a1c4fc9b2ff81568c83e21392a82f344ea9d23da906e4f6a52662764545e19d4"

PYPI_PACKAGE = "aiofiles"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-asyncio \
"
