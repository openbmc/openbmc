SUMMARY = "JSON Web Token implementation in Python"
DESCRIPTION = "A Python implementation of JSON Web Token draft 32.\
 Original implementation was written by https://github.com/progrium"
HOMEPAGE = "http://github.com/jpadilla/pyjwt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=68626705a7b513ca8d5f44a3e200ed0c"

SRC_URI[sha256sum] = "fba44e7898bbca160a2b2b501f492824fc8382485d3a6f11ba5d0c1937ce6130"

PYPI_PACKAGE = "PyJWT"
inherit pypi setuptools3

RDEPENDS:${PN} = "${PYTHON_PN}-cryptography"

BBCLASSEXTEND = "native nativesdk"
