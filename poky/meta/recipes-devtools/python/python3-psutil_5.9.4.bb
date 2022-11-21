SUMMARY = "A cross-platform process and system utilities module for Python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e35fd9f271d19d5f742f20a9d1f8bb8b"
HOMEPAGE = "https://pypi.org/project/psutil/"

SRC_URI[sha256sum] = "3d7f9739eb435d4b1338944abe23f49584bde5395f27487d2ee25ad9a8774a62"

inherit pypi setuptools3

PACKAGES =+ "${PN}-tests"

FILES:${PN}-tests += " \
    ${PYTHON_SITEPACKAGES_DIR}/psutil/test* \
    ${PYTHON_SITEPACKAGES_DIR}/psutil/__pycache__/test* \
"


RDEPENDS:${PN} += " \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-xml \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-ctypes \
    ${PYTHON_PN}-resource \
"

BBCLASSEXTEND = "native nativesdk"
