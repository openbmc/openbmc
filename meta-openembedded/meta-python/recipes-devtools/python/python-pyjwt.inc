SUMMARY = "JSON Web Token implementation in Python"
SUMMARY = "JSON Web Token implementation in Python"
DESCRIPTION = "A Python implementation of JSON Web Token draft 32.\
 Original implementation was written by https://github.com/progrium"
HOMEPAGE = "http://github.com/jpadilla/pyjwt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=68626705a7b513ca8d5f44a3e200ed0c"

SRC_URI[md5sum] = "f7dbe0d62f2087a7605946aeeca02541"
SRC_URI[sha256sum] = "dacba5786fe3bf1a0ae8673874e29f9ac497860955c501289c63b15d3daae63a"

PYPI_PACKAGE = "PyJWT"
inherit pypi

RDEPENDS_${PN} = "${PYTHON_PN}-cryptography"

BBCLASSEXTEND = "native nativesdk"
