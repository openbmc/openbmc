SUMMARY = "Dominate is a Python library for creating and manipulating HTML documents using an elegant DOM API."
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b52f2d57d10c4f7ee67a7eb9615d5d24"

SRC_URI[md5sum] = "35eeb6b5587c8c9a51cd22c83e07ac49"
SRC_URI[sha256sum] = "9b05481605ea8c0afd0a98c0156a9fb78d9c406368d66b3e6fedf36920fb9d78"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-threading \
    "
