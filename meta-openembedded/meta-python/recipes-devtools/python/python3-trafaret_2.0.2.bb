SUMMARY = "Ultimate transformation library that supports validation, contexts and aiohttp."

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=21ecc7aa8f699874e706fc1354903437"

SRC_URI[sha256sum] = "61dfc25b574f70bfdf7ee3a808ec423061811c13a10b8d3c9e11ab539b96ab65"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-urllib3 \
"

BBCLASSEXTEND = "native nativesdk"
