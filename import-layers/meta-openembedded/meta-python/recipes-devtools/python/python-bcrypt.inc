DESCRIPTION = "Modern password hashing for your software and your servers."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8f7bb094c7232b058c7e9f2e431f389c"

DEPENDS += "${PYTHON_PN}-cffi-native"

SRC_URI[md5sum] = "8408abc974446e64862a9742104e97b6"
SRC_URI[sha256sum] = "67ed1a374c9155ec0840214ce804616de49c3df9c5bc66740687c1c9b1cd9e8d"

inherit pypi

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-cffi \
    ${PYTHON_PN}-ctypes \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-six \
"
