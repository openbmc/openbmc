SUMMARY = "JSON Web Token implementation in Python"
DESCRIPTION = "A Python implementation of JSON Web Token draft 32.\
 Original implementation was written by https://github.com/progrium"
HOMEPAGE = "https://github.com/jpadilla/pyjwt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e4b56d2c9973d8cf54655555be06e551"

SRC_URI[sha256sum] = "35f95c1f0fbe5d5ba6e43f00271c275f7a1a4db1dab27bf708073b75318ea623"

PYPI_PACKAGE = "pyjwt"
CVE_PRODUCT = "pyjwt"
CVE_STATUS[CVE-2025-45768] = "disputed: vulnerability can be avoided if the library is used correctly"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "\
    python3-cryptography \
    python3-json \
"

BBCLASSEXTEND = "native nativesdk"
