SUMMARY = "Ultimate transformation library that supports validation, contexts and aiohttp."

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=21ecc7aa8f699874e706fc1354903437"

SRC_URI[sha256sum] = "72c342ede27fb34cd219b62855119380ae1c87006b6106be163c01159eb955ff"
SRC_URI[md5sum] = "228675376012fb27dd7f70ab685e7766"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-urllib3 \
"

BBCLASSEXTEND = "native nativesdk"
