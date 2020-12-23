DESCRIPTION = "pytest-asyncio is an Apache2 licensed library, written in Python, for testing asyncio code with pytest"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

SRC_URI[md5sum] = "b63593bc08f445f6e3f14c34128a68ed"
SRC_URI[sha256sum] = "9882c0c6b24429449f5f969a5158b528f39bde47dc32e85b9f0403965017e700"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-pytest \
"

BBCLASSEXTEND = "native nativesdk"
