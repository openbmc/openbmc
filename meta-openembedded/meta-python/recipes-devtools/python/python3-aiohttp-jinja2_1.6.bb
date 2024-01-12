SUMMARY = "jinja2 template renderer for aiohttp.web (http server for asyncio)"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=29dca541e03964615590ca7b50392d97"

SRC_URI[sha256sum] = "a3a7ff5264e5bca52e8ae547bbfd0761b72495230d438d05b6c0915be619b0e2"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-jinja2 \
    ${PYTHON_PN}-aiohttp \
"
