DESCRIPTION = "Modern password hashing for your software and your servers."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8f7bb094c7232b058c7e9f2e431f389c"

DEPENDS += "${PYTHON_PN}-cffi-native"

SRC_URI[md5sum] = "20da8b40790caad99c4086dba533154b"
SRC_URI[sha256sum] = "6645c8d0ad845308de3eb9be98b6fd22a46ec5412bfc664a423e411cdd8f5488"

inherit pypi

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-cffi \
    ${PYTHON_PN}-six \
"
