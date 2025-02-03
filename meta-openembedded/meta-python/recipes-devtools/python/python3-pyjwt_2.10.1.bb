SUMMARY = "JSON Web Token implementation in Python"
DESCRIPTION = "A Python implementation of JSON Web Token draft 32.\
 Original implementation was written by https://github.com/progrium"
HOMEPAGE = "http://github.com/jpadilla/pyjwt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e4b56d2c9973d8cf54655555be06e551"

SRC_URI[sha256sum] = "3cc5772eb20009233caf06e9d8a0577824723b44e6648ee0a2aedb6cf9381953"

PYPI_PACKAGE = "pyjwt"
inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "\
    python3-cryptography \
    python3-json \
"

BBCLASSEXTEND = "native nativesdk"
