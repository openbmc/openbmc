SUMMARY = "File support for asyncio"
DESCRIPTION = "Asynchronous local file IO library for asyncio and Python"
HOMEPAGE = "https://github.com/aio-libs/aiohttp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

SRC_URI[sha256sum] = "22a075c9e5a3810f0c2e48f3008c94d68c65d763b9b03857924c99e57355166c"

PYPI_PACKAGE = "aiofiles"

inherit pypi python_hatchling

RDEPENDS:${PN} = "\
    python3-asyncio \
"
