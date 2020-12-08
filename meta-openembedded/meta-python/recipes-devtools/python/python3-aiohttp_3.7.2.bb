SUMMARY = "Async http client/server framework"
DESCRIPTION = "Asynchronous HTTP client/server framework for asyncio and Python"
HOMEPAGE = "https://github.com/aio-libs/aiohttp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3bf3d48554bdca1ea7fdb48de378c2ca"

SRC_URI[md5sum] = "dcf770341241aa09340653a1562ca816"
SRC_URI[sha256sum] = "c6da1af59841e6d43255d386a2c4bfb59c0a3b262bdb24325cc969d211be6070"

PYPI_PACKAGE = "aiohttp"
inherit setuptools3 pypi
RDEPENDS_${PN} = "\
    ${PYTHON_PN}-async-timeout \
    ${PYTHON_PN}-attrs \
    ${PYTHON_PN}-chardet \
    ${PYTHON_PN}-idna-ssl \
    ${PYTHON_PN}-misc \
    ${PYTHON_PN}-multidict \
    ${PYTHON_PN}-typing \
    ${PYTHON_PN}-yarl \
"
