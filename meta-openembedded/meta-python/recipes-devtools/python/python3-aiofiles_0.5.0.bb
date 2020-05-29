SUMMARY = "File support for asyncio"
DESCRIPTION = "Asynchronous local file IO library for asyncio and Python"
HOMEPAGE = "https://github.com/aio-libs/aiohttp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

SRC_URI[md5sum] = "2243eff06072115e8afe8907677ca51d"
SRC_URI[sha256sum] = "98e6bcfd1b50f97db4980e182ddd509b7cc35909e903a8fe50d8849e02d815af"

PYPI_PACKAGE = "aiofiles"

inherit pypi setuptools3

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-asyncio \
"
