SUMMARY = "jinja2 template renderer for aiohttp.web (http server for asyncio)"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=29dca541e03964615590ca7b50392d97"

SRC_URI[sha256sum] = "9c22a0e48e3b277fc145c67dd8c3b8f609dab36bce9eb337f70dfe716663c9a0"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-jinja2 \
    ${PYTHON_PN}-aiohttp \
"

BBCLASSEXTEND = "native nativesdk"
