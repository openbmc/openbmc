SUMMARY = "JSON Web Token implementation in Python"
DESCRIPTION = "A Python implementation of JSON Web Token draft 32.\
 Original implementation was written by https://github.com/progrium"
HOMEPAGE = "https://github.com/jpadilla/pyjwt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e4b56d2c9973d8cf54655555be06e551"

SRC_URI[sha256sum] = "41571c89ca91598c79e8ef18a2d07367d4810fbbd6f637794879baf1b7703423"

PYPI_PACKAGE = "pyjwt"
CVE_PRODUCT = "pyjwt"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "\
    python3-cryptography \
    python3-json \
"

BBCLASSEXTEND = "native nativesdk"

