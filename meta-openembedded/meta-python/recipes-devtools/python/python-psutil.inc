SUMMARY = "A cross-platform process and system utilities module for Python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0f02e99f7f3c9a7fe8ecfc5d44c2be62"

SRC_URI[md5sum] = "765a4179f02c149794a6e99d890dfa4b"
SRC_URI[sha256sum] = "fa0a570e0a30b9dd618bffbece590ae15726b47f9f1eaf7518dfb35f4d7dcd21"

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
