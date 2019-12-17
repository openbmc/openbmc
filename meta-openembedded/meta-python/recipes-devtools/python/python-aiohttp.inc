SUMMARY = "Async http client/server framework"
DESCRIPTION = "Asynchronous HTTP client/server framework for asyncio and Python"
HOMEPAGE = "https://github.com/aio-libs/aiohttp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=cf056e8e7a0a5477451af18b7b5aa98c"

SRC_URI[md5sum] = "85fe5c9037256c58d4678148bd91b3f3"
SRC_URI[sha256sum] = "9c4c83f4fa1938377da32bc2d59379025ceeee8e24b89f72fcbccd8ca22dc9bf"

PYPI_PACKAGE = "aiohttp"
inherit pypi
RDEPENDS_${PN} = "\
    ${PYTHON_PN}-async-timeout \
    ${PYTHON_PN}-chardet \
    ${PYTHON_PN}-idna-ssl \
    ${PYTHON_PN}-multidict \
    ${PYTHON_PN}-yarl \
    ${PYTHON_PN}-typing \
    ${PYTHON_PN}-misc \
    ${PYTHON_PN}-attrs \
"
