SUMMARY = "jinja2 template renderer for aiohttp.web (http server for asyncio)"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c82758543767c96574b6e29fc478fb73"

SRC_URI[sha256sum] = "2dfe29cfd278d07cd0a851afb98471bc8ce2a830968443e40d67636f3c035d79"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-jinja2 \
    ${PYTHON_PN}-aiohttp \
"

BBCLASSEXTEND = "native nativesdk"
