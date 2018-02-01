SUMMARY = "A cross-platform process and system utilities module for Python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0f02e99f7f3c9a7fe8ecfc5d44c2be62"

SRC_URI[md5sum] = "c9aa2599dcd9e5b59d71b6660d396062"
SRC_URI[sha256sum] = "2fc91d068faa5613c093335f0e758673ef8c722ad4bfa4aded64c13ae69089eb"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-subprocess \
    ${PYTHON_PN}-threading \
"
