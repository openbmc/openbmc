SUMMARY = "JSON Web Token implementation in Python"
DESCRIPTION = "A Python implementation of JSON Web Token draft 32.\
 Original implementation was written by https://github.com/progrium"
HOMEPAGE = "http://github.com/jpadilla/pyjwt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e4b56d2c9973d8cf54655555be06e551"

SRC_URI[sha256sum] = "57e28d156e3d5c10088e0c68abb90bfac3df82b40a71bd0daa20c65ccd5c23de"

PYPI_PACKAGE = "PyJWT"
inherit pypi setuptools3

RDEPENDS:${PN} = " \
    python3-json \
    python3-cryptography \
"

BBCLASSEXTEND = "native nativesdk"
