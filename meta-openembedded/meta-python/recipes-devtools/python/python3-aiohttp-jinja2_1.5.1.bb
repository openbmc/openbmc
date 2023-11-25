SUMMARY = "jinja2 template renderer for aiohttp.web (http server for asyncio)"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=29dca541e03964615590ca7b50392d97"

SRC_URI[sha256sum] = "8d149b2a57d91f794b33a394ea5bc66b567f38c74a5a6a9477afc2450f105c01"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-jinja2 \
    ${PYTHON_PN}-aiohttp \
"
