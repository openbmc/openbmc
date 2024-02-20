SUMMARY = "Async http client/server framework"
DESCRIPTION = "Asynchronous HTTP client/server framework for asyncio and Python"
HOMEPAGE = "https://github.com/aio-libs/aiohttp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=748073912af33aa59430d3702aa32d41"

SRC_URI[sha256sum] = "90842933e5d1ff760fae6caca4b2b3edba53ba8f4b71e95dacf2818a2aca06f7"

PYPI_PACKAGE = "aiohttp"
inherit python_setuptools_build_meta pypi

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-aiohappyeyeballs \
    ${PYTHON_PN}-aiosignal \
    ${PYTHON_PN}-async-timeout \
    ${PYTHON_PN}-frozenlist \
    ${PYTHON_PN}-multidict \
    ${PYTHON_PN}-yarl \
    ${PYTHON_PN}-aiodns \
"
