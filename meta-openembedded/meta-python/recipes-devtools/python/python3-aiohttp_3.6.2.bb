SUMMARY = "Async http client/server framework"
DESCRIPTION = "Asynchronous HTTP client/server framework for asyncio and Python"
HOMEPAGE = "https://github.com/aio-libs/aiohttp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=cf056e8e7a0a5477451af18b7b5aa98c"

SRC_URI[md5sum] = "ca40144c199a09fc1a141960cf6295f0"
SRC_URI[sha256sum] = "259ab809ff0727d0e834ac5e8a283dc5e3e0ecc30c4d80b3cd17a4139ce1f326"

PYPI_PACKAGE = "aiohttp"
inherit setuptools3 pypi
RDEPENDS_${PN} = "\
    ${PYTHON_PN}-async-timeout \
    ${PYTHON_PN}-attrs \
    ${PYTHON_PN}-chardet \
    ${PYTHON_PN}-html \
    ${PYTHON_PN}-idna-ssl \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-misc \
    ${PYTHON_PN}-multidict \
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-yarl \
"
