SUMMARY = "Dominate is a Python library for creating and manipulating HTML documents using an elegant DOM API."
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b52f2d57d10c4f7ee67a7eb9615d5d24"

SRC_URI[md5sum] = "45bd97e6f7888aac24ae86013c57638e"
SRC_URI[sha256sum] = "4b8ce6f33633c9dd9175b228d21c00c801b6bd0327747cd5e17fc2da934c3a69"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-threading \
    "
