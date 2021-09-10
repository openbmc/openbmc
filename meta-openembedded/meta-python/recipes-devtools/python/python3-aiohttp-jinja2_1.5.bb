SUMMARY = "jinja2 template renderer for aiohttp.web (http server for asyncio)"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=29dca541e03964615590ca7b50392d97"

SRC_URI[sha256sum] = "7c3ba5eac060b691f4e50534af2d79fca2a75712ebd2b25e6fcb1295859f910b"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-jinja2 \
    ${PYTHON_PN}-aiohttp \
"

BBCLASSEXTEND = "native nativesdk"
