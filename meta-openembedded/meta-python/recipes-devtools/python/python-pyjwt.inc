SUMMARY = "JSON Web Token implementation in Python"
SUMMARY = "JSON Web Token implementation in Python"
DESCRIPTION = "A Python implementation of JSON Web Token draft 32.\
 Original implementation was written by https://github.com/progrium"
HOMEPAGE = "http://github.com/jpadilla/pyjwt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=68626705a7b513ca8d5f44a3e200ed0c"

SRC_URI[md5sum] = "2731c6b66b67b95d1944f072f5fa35a9"
SRC_URI[sha256sum] = "4ee413b357d53fd3fb44704577afac88e72e878716116270d722723d65b42176"

PYPI_PACKAGE = "PyJWT"
inherit pypi

RDEPENDS_${PN} = "${PYTHON_PN}-cryptography"

BBCLASSEXTEND = "native nativesdk"
