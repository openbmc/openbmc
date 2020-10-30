SUMMARY = "Async http client/server framework"
DESCRIPTION = "Asynchronous HTTP client/server framework for asyncio and Python"
HOMEPAGE = "https://github.com/aio-libs/aiohttp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3bf3d48554bdca1ea7fdb48de378c2ca"

SRC_URI[md5sum] = "ed78633cc420b29d3b61c7d877dc0901"
SRC_URI[sha256sum] = "04f9d70f6c4d089be5068d7df6281e638f6820d4f1b1ec3dc012b0b43fa997d2"

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
