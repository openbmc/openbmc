DESCRIPTION = "Human friendly output for text interfaces using Python"
HOMEPAGE = "https://humanfriendly.readthedocs.io/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=690da298a43805797a4fa7bbe180b3c6"

PYPI_PACKAGE = "humanfriendly"

SRC_URI[md5sum] = "9573f9f37a5454d8205cfd1b9b8db9d2"
SRC_URI[sha256sum] = "33ee8ceb63f1db61cce8b5c800c531e1a61023ac5488ccde2ba574a85be00a85"

inherit pypi

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
