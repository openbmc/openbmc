SUMMARY = "A cross-platform process and system utilities module for Python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0f02e99f7f3c9a7fe8ecfc5d44c2be62"

SRC_URI[md5sum] = "529244aea5d32fb0c2ea46d80ce54abc"
SRC_URI[sha256sum] = "12dd9c8abbad15f055e9579130035b38617020ce176f4a498b7870e6321ffa67"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-subprocess \
    ${PYTHON_PN}-threading \
"
