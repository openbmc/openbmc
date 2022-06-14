SUMMARY = "JSON Web Token implementation in Python"
DESCRIPTION = "A Python implementation of JSON Web Token draft 32.\
 Original implementation was written by https://github.com/progrium"
HOMEPAGE = "http://github.com/jpadilla/pyjwt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e4b56d2c9973d8cf54655555be06e551"

SRC_URI[sha256sum] = "d42908208c699b3b973cbeb01a969ba6a96c821eefb1c5bfe4c390c01d67abba"

PYPI_PACKAGE = "PyJWT"
inherit pypi setuptools3

RDEPENDS:${PN} = "${PYTHON_PN}-cryptography"

BBCLASSEXTEND = "native nativesdk"
