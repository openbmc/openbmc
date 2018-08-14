SUMMARY = "A cross-platform process and system utilities module for Python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0f02e99f7f3c9a7fe8ecfc5d44c2be62"

SRC_URI[md5sum] = "3b291833dbea631db9d271aa602a169a"
SRC_URI[sha256sum] = "e2467e9312c2fa191687b89ff4bc2ad8843be4af6fb4dc95a7cc5f7d7a327b18"

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
