SUMMARY = "A cross-platform process and system utilities module for Python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e35fd9f271d19d5f742f20a9d1f8bb8b"

SRC_URI[md5sum] = "b2c94e0368fc6e6f237a7252f8e85ca2"
SRC_URI[sha256sum] = "ffad8eb2ac614518bbe3c0b8eb9dffdb3a8d2e3a7d5da51c5b974fb723a5c5aa"

PACKAGES =+ "${PN}-tests"

FILES_${PN}-tests += " \
    ${PYTHON_SITEPACKAGES_DIR}/psutil/test* \
    ${PYTHON_SITEPACKAGES_DIR}/psutil/__pycache__/test* \
"


RDEPENDS_${PN} += " \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-xml \
"

BBCLASSEXTEND = "native nativesdk"
