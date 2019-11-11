SUMMARY = "Dominate is a Python library for creating and manipulating HTML documents using an elegant DOM API."
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b52f2d57d10c4f7ee67a7eb9615d5d24"

SRC_URI[md5sum] = "27d3aa7e0d67902c505e248e705e5e2e"
SRC_URI[sha256sum] = "a92474b4312bd8b4c1789792f3ec8c571cd8afa8e7502a2b1c64dd48cd67e59c"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-threading \
    "
