SUMMARY = "A cross-platform process and system utilities module for Python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e35fd9f271d19d5f742f20a9d1f8bb8b"
HOMEPAGE = "https://pypi.org/project/psutil/"

SRC_URI[sha256sum] = "869842dbd66bb80c3217158e629d6fceaecc3a3166d3d1faee515b05dd26ca25"

inherit pypi setuptools3

SRC_URI += "file://0001-fix-failure-test-cases.patch"

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
