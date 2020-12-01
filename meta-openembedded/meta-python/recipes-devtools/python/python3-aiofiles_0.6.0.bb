SUMMARY = "File support for asyncio"
DESCRIPTION = "Asynchronous local file IO library for asyncio and Python"
HOMEPAGE = "https://github.com/aio-libs/aiohttp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

SRC_URI[md5sum] = "3b7ba03babd3d3a6101524469358843e"
SRC_URI[sha256sum] = "e0281b157d3d5d59d803e3f4557dcc9a3dff28a4dd4829a9ff478adae50ca092"

PYPI_PACKAGE = "aiofiles"

inherit pypi setuptools3

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-asyncio \
"
