DESCRIPTION = "Human friendly output for text interfaces using Python"
HOMEPAGE = "https://humanfriendly.readthedocs.io/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=764e737b117a38d773609885e8d04f0b"

PYPI_PACKAGE = "humanfriendly"

SRC_URI[md5sum] = "e6064a6fe099c4231c3e969ca5fea335"
SRC_URI[sha256sum] = "bf52ec91244819c780341a3438d5d7b09f431d3f113a475147ac9b7b167a3d12"

inherit pypi setuptools3

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-fcntl \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-math \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-stringold \
"

BBCLASSEXTEND = "native"
