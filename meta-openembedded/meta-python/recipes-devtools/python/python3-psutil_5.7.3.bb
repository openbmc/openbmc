SUMMARY = "A cross-platform process and system utilities module for Python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e35fd9f271d19d5f742f20a9d1f8bb8b"

SRC_URI[md5sum] = "b07a067e6a930608235f4e5d9b1f90f5"
SRC_URI[sha256sum] = "af73f7bcebdc538eda9cc81d19db1db7bf26f103f91081d780bbacfcb620dee2"

inherit pypi setuptools3

PACKAGES =+ "${PN}-tests"

FILES_${PN}-tests += " \
    ${PYTHON_SITEPACKAGES_DIR}/psutil/test* \
    ${PYTHON_SITEPACKAGES_DIR}/psutil/__pycache__/test* \
"


RDEPENDS_${PN} += " \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-xml \
    ${PYTHON_PN}-netclient \
"

BBCLASSEXTEND = "native nativesdk"
