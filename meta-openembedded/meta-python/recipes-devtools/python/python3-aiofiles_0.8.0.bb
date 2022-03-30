SUMMARY = "File support for asyncio"
DESCRIPTION = "Asynchronous local file IO library for asyncio and Python"
HOMEPAGE = "https://github.com/aio-libs/aiohttp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

SRC_URI[sha256sum] = "8334f23235248a3b2e83b2c3a78a22674f39969b96397126cc93664d9a901e59"

PYPI_PACKAGE = "aiofiles"

inherit pypi python_poetry_core

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-asyncio \
"
