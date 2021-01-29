SUMMARY = "JSON Web Token implementation in Python"
DESCRIPTION = "A Python implementation of JSON Web Token draft 32.\
 Original implementation was written by https://github.com/progrium"
HOMEPAGE = "http://github.com/jpadilla/pyjwt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=68626705a7b513ca8d5f44a3e200ed0c"

SRC_URI[sha256sum] = "a5c70a06e1f33d81ef25eecd50d50bd30e34de1ca8b2b9fa3fe0daaabcf69bf7"

PYPI_PACKAGE = "PyJWT"
inherit pypi setuptools3

RDEPENDS_${PN} = "${PYTHON_PN}-cryptography"

BBCLASSEXTEND = "native nativesdk"
