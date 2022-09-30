SUMMARY = "File support for asyncio"
DESCRIPTION = "Asynchronous local file IO library for asyncio and Python"
HOMEPAGE = "https://github.com/aio-libs/aiohttp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

SRC_URI[sha256sum] = "9107f1ca0b2a5553987a94a3c9959fe5b491fdf731389aa5b7b1bd0733e32de6"

PYPI_PACKAGE = "aiofiles"

inherit pypi python_poetry_core

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-asyncio \
"
